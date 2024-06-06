package com.syrtsiob.worknet.enums;

public enum EmploymentType {
    FULL_TIME,
    PART_TIME,
    CONTRACT;

    public static String[] getEmploymentTypes() {
        EmploymentType[] values = EmploymentType.values();
        String[] array = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            array[i] = values[i].toString();
        }

        return array;
    }
}
