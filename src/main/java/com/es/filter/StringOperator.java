package com.es.filter;

public enum StringOperator implements Operator {
    EQ(1),
    LIKE(1),
    EXISTS(1);


    int minimumOperandsRequired;

    StringOperator(int minimumOperandsRequired) {
        this.minimumOperandsRequired = minimumOperandsRequired;
    }

    @Override
    public int getMinimumOperandsRequired() {
        return minimumOperandsRequired;
    }


    static StringOperator getEnum(String operator) throws Exception {
        try {
            return StringOperator.valueOf(operator.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new Exception();
        }
    }
}
