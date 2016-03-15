package com.busticket.amedora.busticketsrl.model;

/**
 * Created by USER on 1/20/2016.
 */
public class Trip {
    public int id;
    public String trip_ID;
    public int driverID;
    public String fullname;
    public String destFrom;
    public String destTo;
    public String startTime;
    public String endTime;
    public String totalHrs;
    public String speedoStart;
    public String speedoEnd;
    public String fuelLevel;
    public String oilLevel;
    public String brakeFluidLevel;
    public int busID;
    public int status;
    public int passenger;
    public String remark;
    public int serviceID;
    public int view;
    public String createdAt;
    public String updatedAt;
    public int routeID;
    public int scheduleID;
    public String tripDate;
    public int tripCount;

    public Trip(){

    }
    public int getId() {
        return id;
    }
    public String getTrip_ID() {
        return trip_ID;
    }
    public int getDriverID() {
        return driverID;
    }
    public String getFullname() {
        return fullname;
    }
    public String getDestFrom() {
        return destFrom;
    }
    public String getDestTo() {
        return destTo;
    }
    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public String getTotalHrs() {
        return totalHrs;
    }
    public String getSpeedoStart() {
        return speedoStart;
    }
    public String getSpeedoEnd() {
        return speedoEnd;
    }
    public String getFuelLevel() {
        return fuelLevel;
    }
    public String getOilLevel() {
        return oilLevel;
    }
    public String getBrakeFluidLevel() {
        return brakeFluidLevel;
    }
    public int getBusID() {
        return busID;
    }
    public int getStatus() {
        return status;
    }
    public int getPassenger() {
        return passenger;
    }
    public String getRemark() {
        return remark;
    }
    public int getServiceID() {
        return serviceID;
    }

    public String getTripDate() {
        return tripDate;
    }

    public int getView() {
        return view;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }

    public int getRouteID() {
        return routeID;
    }

    public int getTripCount() {
        return tripCount;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setTrip_ID(String trip_ID) {
        this.trip_ID = trip_ID;
    }
    public void setDriverID(int driverID) {
        this.driverID = driverID;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public void setDestFrom(String destFrom) {
        this.destFrom = destFrom;
    }
    public void setDestTo(String destTo) {
        this.destTo = destTo;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public void setTotalHrs(String totalHrs) {
        this.totalHrs = totalHrs;
    }
    public void setSpeedoStart(String speedoStart) {
        this.speedoStart = speedoStart;
    }
    public void setSpeedoEnd(String speedoEnd) {
        this.speedoEnd = speedoEnd;
    }
    public void setFuelLevel(String fuelLevel) {
        this.fuelLevel = fuelLevel;
    }
    public void setOilLevel(String oilLevel) {
        this.oilLevel = oilLevel;
    }
    public void setBrakeFluidLevel(String brakeFluidLevel) {
        this.brakeFluidLevel = brakeFluidLevel;
    }
    public void setBusID(int busID) {
        this.busID = busID;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public void setPassenger(int passenger) {
        this.passenger = passenger;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }
    public void setView(int view) {
        this.view = view;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public void setTripCount(int tripCount) {
        this.tripCount = tripCount;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("{\"id\":\""+this.getId()+"\",")
                .append("\"trip_id\":\""+this.getTrip_ID()+ "\",")
                .append("\"driver_id\":\"" + this.getDriverID() + "\",")
                .append("\"fullname\":\"" + this.getFullname() + "\",")
                .append("\"destination_from\":\"" + this.getDestFrom() + "\",")
                .append("\"destination_to\":\""+this.getDestTo()+"\",")
                .append("\"start_time\":\"" + this.getStartTime() + "\",")
                .append("\"end_time\":\"" + this.getEndTime() + "\",")
                .append("\"total_hrs\":\"" + this.getTotalHrs() + "\",")
                .append("\"speedometer_start\":\"" + this.getSpeedoStart() + "\"")

                .append("\"speedometer_end\":\"" + this.getSpeedoEnd() + "\",")
                .append("\"fuel_level\":\"" + this.getFuelLevel() + "\",")
                .append("\"oil_level\":\"" + this.getOilLevel() + "\"")
                .append("\"brake_fluid_level\":\"" + this.getBrakeFluidLevel() + "\",")
                .append("\"bus_id\":\"" + this.getBusID() + "\",")
                .append("\"status\":\"" + this.getStatus() + "\"")

                .append("\"passenger\":\"" + this.getPassenger() + "\",")
                .append("\"remark\":\"" + this.getRemark() + "\",")
                .append("\"service_id\":\"" + this.getServiceID() + "\"")
                .append("\"trip_count\":\"" + this.getTripCount() + "\"")
                .append("\"view\":\"" + this.getView() + "\",")
                .append("\"created_at\":\"" + this.getCreatedAt() + "\",")
                .append("\"updated_at\":\"" + this.getUpdatedAt() + "\"")

                .append("}").toString();
        //return super.toString();
    }
}

