# Flow to mermaid

```mermaid
graph TD
0((START<br/>开始))-->2{{IS_PAY<br/>是否支付}}
2-->|"next == 'yes'"| 6{{PAY_DONE<br/>支付完成}}
2-->|"next == 'no'"| SOWAIT_PAY[WAIT_PAY<br/>等待支付]
6-->|"so.switch == 1"| SOSWITCH_1[SWITCH_1<br/>switch1]
6-->|"so.switch == 2"| SOSWITCH_2[SWITCH_2<br/>switch2]
6-->|"so.switch == 3"| SOSWITCH_3[SWITCH_3<br/>switch3]
6-->SOSWITCH_DEFAULT[SWITCH_DEFAULT<br/>switch_default]
style 0 fill:#fff,stroke:#333,stroke-width:4px,color:#333
style SOWAIT_PAY fill:#999,stroke:#bbb,stroke-width:4px,color:#fff
style SOSWITCH_1 fill:#999,stroke:#bbb,stroke-width:4px,color:#fff
style SOSWITCH_2 fill:#999,stroke:#bbb,stroke-width:4px,color:#fff
style SOSWITCH_3 fill:#999,stroke:#bbb,stroke-width:4px,color:#fff
style SOSWITCH_DEFAULT fill:#999,stroke:#bbb,stroke-width:4px,color:#fff
```
