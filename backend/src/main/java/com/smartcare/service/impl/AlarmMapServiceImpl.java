package com.smartcare.service.impl;

import com.smartcare.mapper.AlarmMapMapper;
import com.smartcare.service.AlarmMapService;
import com.smartcare.vo.AlarmMapVO;
import com.smartcare.websocket.AlarmWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlarmMapServiceImpl implements AlarmMapService {

    private final AlarmMapMapper alarmMapMapper;
    private final AlarmWebSocketHandler alarmWebSocketHandler;

    @Override
    public List<AlarmMapVO> listAlarmMap(Byte status) {
        return alarmMapMapper.selectAlarmMapList(status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long eventId, Byte status, Long processedBy, String processNotes) {
        alarmMapMapper.updateEventStatus(eventId, status, processedBy, processNotes);

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "ALARM_STATUS_CHANGED");
        payload.put("eventId", eventId);
        payload.put("status", status);
        alarmWebSocketHandler.broadcast(payload);
    }

    @Override
    public void pushNewAlarm(Long eventId) {
        List<AlarmMapVO> list = alarmMapMapper.selectAlarmMapList(null);
        AlarmMapVO target = list.stream()
                .filter(item -> eventId.equals(item.getEventId()))
                .findFirst()
                .orElse(null);

        if (target == null) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "NEW_ALARM");
        payload.put("data", target);
        alarmWebSocketHandler.broadcast(payload);
    }
}
