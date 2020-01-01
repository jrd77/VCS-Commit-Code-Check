package com.atzuche.config.common.entity;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/30 3:59 下午
 **/
public class HolidaySettingEntity implements Serializable {
    private Integer id;
    private String name;
    private Integer type;
    private String title;
    private String url;
    private String description;
    private Integer legalStartDate;
    private Integer legalEndDate;
    private Integer realStartDate;
    private Integer realEndDate;
    private Integer circle;
    private Integer priority;
    private Integer isShow;
    private String defaultValue;
    private Integer minRentTime;
    private Integer firstCityPackageMinRent;
    private Integer otherCityPackageMinRent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLegalStartDate() {
        return legalStartDate;
    }

    public void setLegalStartDate(Integer legalStartDate) {
        this.legalStartDate = legalStartDate;
    }

    public Integer getLegalEndDate() {
        return legalEndDate;
    }

    public void setLegalEndDate(Integer legalEndDate) {
        this.legalEndDate = legalEndDate;
    }

    public Integer getRealStartDate() {
        return realStartDate;
    }

    public void setRealStartDate(Integer realStartDate) {
        this.realStartDate = realStartDate;
    }

    public Integer getRealEndDate() {
        return realEndDate;
    }

    public void setRealEndDate(Integer realEndDate) {
        this.realEndDate = realEndDate;
    }

    public Integer getCircle() {
        return circle;
    }

    public void setCircle(Integer circle) {
        this.circle = circle;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Integer getMinRentTime() {
        return minRentTime;
    }

    public void setMinRentTime(Integer minRentTime) {
        this.minRentTime = minRentTime;
    }

    public Integer getFirstCityPackageMinRent() {
        return firstCityPackageMinRent;
    }

    public void setFirstCityPackageMinRent(Integer firstCityPackageMinRent) {
        this.firstCityPackageMinRent = firstCityPackageMinRent;
    }

    public Integer getOtherCityPackageMinRent() {
        return otherCityPackageMinRent;
    }

    public void setOtherCityPackageMinRent(Integer otherCityPackageMinRent) {
        this.otherCityPackageMinRent = otherCityPackageMinRent;
    }
}
