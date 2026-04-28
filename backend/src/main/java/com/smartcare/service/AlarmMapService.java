package com.smartcare.service;

import com.smartcare.vo.AlarmMapVO;

import java.util.List;

public interface AlarmMapService {

    List<AlarmMapVO> listAlarmMap(Byte status);

    void updateStatus(Long eventId, Byte status, Long processedBy, String processNotes);

    void pushNewAlarm(Long eventId);
}
