package com.atzuche.order.config.oilprice;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/9 3:48 下午
 **/
public class OilAverageCostEntity implements Serializable {
    private int id;
    private int engineType;
    private int molecule;
    private int denominator;
    private int cityCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEngineType() {
        return engineType;
    }

    public void setEngineType(int engineType) {
        this.engineType = engineType;
    }

    public int getMolecule() {
        return molecule;
    }

    public void setMolecule(int molecule) {
        this.molecule = molecule;
    }

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        this.denominator = denominator;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    @Override
    public String toString() {
        return "OilAverageCostEntity{" +
                "id=" + id +
                ", engineType=" + engineType +
                ", molecule=" + molecule +
                ", denominator=" + denominator +
                ", cityCode=" + cityCode +
                '}';
    }


}
