package com.yhq.bishe.model.domain.enums;

/**
 * @program: bisheBackend-master
 * @description: 队伍状态枚举类
 * @author: HenryYang
 * @create: 2023-03-20 10:46
 **/
public enum TeamStatusEnum {
    PUBLIC(0,"公开"),PRIVATE(1,"私有"),SECRET(2,"加密");
    private int value;
    private String text;

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }
    public static TeamStatusEnum getEnumByValue(Integer value){
        if(value == null){
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum:
             values) {
            if(teamStatusEnum.getValue() == value){
                return teamStatusEnum;
            }

        }
        return null;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}