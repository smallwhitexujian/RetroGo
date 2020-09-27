package com.future.retronet;

public enum  StructType {
    /**
     * struct is:{"code":0,"error":"","result":{"name":"Retro"}}
     */
    Result,
    /**
     * custom struct is: {"code":0,"name":"Retro"}
     */
    Bean,
    /**
     * full custom struct is:{"xxx":"Retro","xxx":"Retro"}
     */
    Direct
}
