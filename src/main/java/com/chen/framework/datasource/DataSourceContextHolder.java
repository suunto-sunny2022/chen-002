package com.chen.framework.datasource;

public final class DataSourceContextHolder {

    private static final ThreadLocal<DataSourceType> CONTEXT = new ThreadLocal<>();

    private DataSourceContextHolder() {
    }

    public static void use(DataSourceType dataSourceType) {
        CONTEXT.set(dataSourceType);
    }

    public static DataSourceType get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
