package com.uutils.plugin;

import android.content.Context;

import com.uutils.utils.ReflectUtils;

public class PushManagerEx {
	ReflectUtils PushManager;

	public PushManagerEx() {
		try {
			PushManager = ReflectUtils.on("com.igexin.sdk.PushManager").call("getInstance");
		} catch (Exception e) {

		}
	}

	static PushManagerEx Instance;

	public static PushManagerEx getInstance() {
		if (Instance == null) {
			Instance = new PushManagerEx();
		}
		return Instance;
	}
	
	public void initialize(Context context){
		try {
			PushManager.call("initialize", context);
		} catch (Exception e) {

		}
	}

	public String getClientid(Context context) {
		String cid = null;
		try {
			cid = PushManager.call("getClientid", context).get();
		} catch (Exception e) {

		}
		return cid;
	}
}
