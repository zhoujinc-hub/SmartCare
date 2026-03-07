# Database Design

## Table: elders

| field | type | description |
|------|------|-------------|
| id | int | primary key |
| name | varchar | elder name |
| age | int | age |
| address | varchar | address |
| contact | varchar | family contact |

## Table: alerts

| field | type | description |
|------|------|-------------|
| id | int | primary key |
| elder_id | int | related elder |
| alert_type | varchar | fall / abnormal |
| alert_time | datetime | time |
| status | varchar | processed/unprocessed |

## Table: events

| field | type | description |
|------|------|-------------|
| id | int | primary key |
| elder_id | int | elder |
| event_type | varchar | event type |
| timestamp | datetime | event time |