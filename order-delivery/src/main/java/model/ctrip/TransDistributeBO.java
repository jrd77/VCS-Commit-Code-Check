package model.ctrip;

import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 *
 */
@Data
@ToString
public class TransDistributeBO {

	private String illegalDeposit;
	private String insure;
	private String abatementInsure;
	private String insureTotalPrices;
	private String abatementTotalInsure;
	private String rentAmt;
	private String totalAmt;
	private String fee;
	private String rentTime;
	private String revertTime;
	private String partnerOrderNo;
	private String holidayRentDays;
	private String holidayAverage;
	private String partnerCode;
	private String realRevertTime;
	private String orderNo;
	
	private String srvNightGetCost;
    private String srvNightReturnCost;
}
