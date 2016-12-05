package com.fjsh.simhash.search.dupcheck.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableData {

    public Integer       ID   = 0;
    public Integer       leadLength = 0;
    public boolean[]     maskList = new boolean[64];
    public Map<Integer, Boolean> leadMap  = new HashMap<Integer, Boolean>();
}
