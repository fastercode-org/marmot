package org.fastercode.marmot.core.alarm;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlarmItemTest extends AlarmItem {
    private String url = "x";
}
