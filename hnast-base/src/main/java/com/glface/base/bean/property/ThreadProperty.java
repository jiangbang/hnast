package com.glface.base.bean.property;

import lombok.Getter;
import lombok.Setter;

/**
 * @author maowei
 */
@Setter
@Getter
public class ThreadProperty {
    private String prefix;
    private int corePoolSize;
    private int maximumPoolSize;
    private int keepAliveTime;
}
