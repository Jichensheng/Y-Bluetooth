package com.heshun.hslibrary.common.config;

import java.io.Serializable;

/**
 * 奔溃日志实体
 * 
 * @author huangxz
 *
 */
public class CrashLog implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String token;
	private String display;
	private String product;
	private String device;
	private String board;
	private String cpuAbi;// cpu
	private String cpuAbi2;
	private String manufacturer;
	private String brand;
	private String model;
	private String bootloader;
	private String radio;
	private String hardware;
	private String serial;// 序列号
	private String type;
	private String tags;
	private String fingerprint;
	private String time;
	private String user;
	private String host;
	private String error;// 错误
	private String width;

	private String height;
	private String dpi;
	private String loginName;
	private String versionName;
	private String versionCode;// 版本号

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getBoard() {
		return board;
	}

	public void setBoard(String board) {
		this.board = board;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getBootloader() {
		return bootloader;
	}

	public void setBootloader(String bootloader) {
		this.bootloader = bootloader;
	}

	public String getRadio() {
		return radio;
	}

	public void setRadio(String radio) {
		this.radio = radio;
	}

	public String getHardware() {
		return hardware;
	}

	public void setHardware(String hardware) {
		this.hardware = hardware;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getDpi() {
		return dpi;
	}

	public void setDpi(String dpi) {
		this.dpi = dpi;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	@Override
	public String toString() {
		return "CrashLog{" + "id='" + id + '\'' + ", display='" + display + '\'' + ", product='" + product + '\''
				+ ", device='" + device + '\'' + ", board='" + board + '\'' + ", cpu_abi='" + cpuAbi + '\''
				+ ", cpu_abi2='" + cpuAbi2 + '\'' + ", manufacturer='" + manufacturer + '\'' + ", brand='" + brand
				+ '\'' + ", model='" + model + '\'' + ", bootloader='" + bootloader + '\'' + ", radio='" + radio + '\''
				+ ", hardware='" + hardware + '\'' + ", serial='" + serial + '\'' + ", type='" + type + '\''
				+ ", tags='" + tags + '\'' + ", fingerprint='" + fingerprint + '\'' + ", time=" + time + ", user='"
				+ user + '\'' + ", host='" + host + '\'' + ", error='" + error + '\'' + ", width=" + width + ", height="
				+ height + ", dpi=" + dpi + ", loginName='" + loginName + '\'' + ", versionName='" + versionName + '\''
				+ ", versionCode='" + versionCode + '\'' + '}';
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCpuAbi() {
		return cpuAbi;
	}

	public void setCpuAbi(String cpuAbi) {
		this.cpuAbi = cpuAbi;
	}

	public String getCpuAbi2() {
		return cpuAbi2;
	}

	public void setCpuAbi2(String cpuAbi2) {
		this.cpuAbi2 = cpuAbi2;
	}
}
