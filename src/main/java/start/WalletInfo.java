package src.main.java.start;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

class Balance {

    String asset;
    double free;
    double locked;

}

class DataInfo {

    Balance[] balances;
    Object totalAssetOfBtc;
    
}

class SnapshotVos {
    public DataInfo data;
    public String type;
    public long updateTime;
}

public class WalletInfo {

    public int code;
    public String msg;
    public SnapshotVos[] snapshotVos;
    
}


