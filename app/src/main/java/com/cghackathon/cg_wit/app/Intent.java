package com.cghackathon.cg_wit.app;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Intent
{
    @SerializedName("name")
    String name;

    @SerializedName("generalResponse")
    String generalResponse;

    @SerializedName("entities")
    List<Entity> entities;
}
