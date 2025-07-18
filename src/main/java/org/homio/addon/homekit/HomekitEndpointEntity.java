package org.homio.addon.homekit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.hapjava.characteristics.ExceptionalConsumer;
import io.github.hapjava.characteristics.impl.airpurifier.CurrentAirPurifierCharacteristic;
import io.github.hapjava.characteristics.impl.airpurifier.TargetAirPurifierStateCharacteristic;
import io.github.hapjava.characteristics.impl.airquality.*;
import io.github.hapjava.characteristics.impl.audio.MuteCharacteristic;
import io.github.hapjava.characteristics.impl.audio.VolumeCharacteristic;
import io.github.hapjava.characteristics.impl.base.BaseCharacteristic;
import io.github.hapjava.characteristics.impl.battery.BatteryLevelCharacteristic;
import io.github.hapjava.characteristics.impl.battery.ChargingStateCharacteristic;
import io.github.hapjava.characteristics.impl.battery.StatusLowBatteryCharacteristic;
import io.github.hapjava.characteristics.impl.carbondioxidesensor.CarbonDioxideDetectedCharacteristic;
import io.github.hapjava.characteristics.impl.carbondioxidesensor.CarbonDioxideLevelCharacteristic;
import io.github.hapjava.characteristics.impl.carbondioxidesensor.CarbonDioxidePeakLevelCharacteristic;
import io.github.hapjava.characteristics.impl.carbonmonoxidesensor.CarbonMonoxideDetectedCharacteristic;
import io.github.hapjava.characteristics.impl.carbonmonoxidesensor.CarbonMonoxideLevelCharacteristic;
import io.github.hapjava.characteristics.impl.carbonmonoxidesensor.CarbonMonoxidePeakLevelCharacteristic;
import io.github.hapjava.characteristics.impl.common.*;
import io.github.hapjava.characteristics.impl.contactsensor.ContactSensorStateCharacteristic;
import io.github.hapjava.characteristics.impl.fan.*;
import io.github.hapjava.characteristics.impl.filtermaintenance.FilterChangeIndicationCharacteristic;
import io.github.hapjava.characteristics.impl.filtermaintenance.FilterLifeLevelCharacteristic;
import io.github.hapjava.characteristics.impl.filtermaintenance.ResetFilterIndicationCharacteristic;
import io.github.hapjava.characteristics.impl.garagedoor.CurrentDoorStateCharacteristic;
import io.github.hapjava.characteristics.impl.garagedoor.TargetDoorStateCharacteristic;
import io.github.hapjava.characteristics.impl.heatercooler.CurrentHeaterCoolerStateCharacteristic;
import io.github.hapjava.characteristics.impl.heatercooler.CurrentHeaterCoolerStateEnum;
import io.github.hapjava.characteristics.impl.heatercooler.TargetHeaterCoolerStateCharacteristic;
import io.github.hapjava.characteristics.impl.heatercooler.TargetHeaterCoolerStateEnum;
import io.github.hapjava.characteristics.impl.humidifier.TargetHumidifierDehumidifierStateCharacteristic;
import io.github.hapjava.characteristics.impl.humiditysensor.CurrentRelativeHumidityCharacteristic;
import io.github.hapjava.characteristics.impl.humiditysensor.TargetRelativeHumidityCharacteristic;
import io.github.hapjava.characteristics.impl.inputsource.InputDeviceTypeCharacteristic;
import io.github.hapjava.characteristics.impl.inputsource.InputSourceTypeCharacteristic;
import io.github.hapjava.characteristics.impl.inputsource.TargetVisibilityStateCharacteristic;
import io.github.hapjava.characteristics.impl.leaksensor.LeakDetectedStateCharacteristic;
import io.github.hapjava.characteristics.impl.lightbulb.BrightnessCharacteristic;
import io.github.hapjava.characteristics.impl.lightbulb.ColorTemperatureCharacteristic;
import io.github.hapjava.characteristics.impl.lightbulb.HueCharacteristic;
import io.github.hapjava.characteristics.impl.lightbulb.SaturationCharacteristic;
import io.github.hapjava.characteristics.impl.lightsensor.CurrentAmbientLightLevelCharacteristic;
import io.github.hapjava.characteristics.impl.lock.LockCurrentStateCharacteristic;
import io.github.hapjava.characteristics.impl.lock.LockTargetStateCharacteristic;
import io.github.hapjava.characteristics.impl.motionsensor.MotionDetectedCharacteristic;
import io.github.hapjava.characteristics.impl.occupancysensor.OccupancyDetectedCharacteristic;
import io.github.hapjava.characteristics.impl.outlet.OutletInUseCharacteristic;
import io.github.hapjava.characteristics.impl.securitysystem.CurrentSecuritySystemStateCharacteristic;
import io.github.hapjava.characteristics.impl.securitysystem.SecuritySystemAlarmTypeCharacteristic;
import io.github.hapjava.characteristics.impl.securitysystem.TargetSecuritySystemStateCharacteristic;
import io.github.hapjava.characteristics.impl.slat.*;
import io.github.hapjava.characteristics.impl.smokesensor.SmokeDetectedCharacteristic;
import io.github.hapjava.characteristics.impl.television.*;
import io.github.hapjava.characteristics.impl.televisionspeaker.VolumeControlTypeCharacteristic;
import io.github.hapjava.characteristics.impl.televisionspeaker.VolumeSelectorCharacteristic;
import io.github.hapjava.characteristics.impl.thermostat.*;
import io.github.hapjava.characteristics.impl.valve.RemainingDurationCharacteristic;
import io.github.hapjava.characteristics.impl.valve.SetDurationCharacteristic;
import io.github.hapjava.characteristics.impl.valve.ValveTypeEnum;
import io.github.hapjava.characteristics.impl.windowcovering.*;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.homio.addon.homekit.annotations.HomekitCharacteristic;
import org.homio.addon.homekit.annotations.HomekitValidValues;
import org.homio.addon.homekit.enums.HomekitAccessoryType;
import org.homio.addon.homekit.enums.HomekitCharacteristicType;
import org.homio.api.ContextVar;
import org.homio.api.ContextVar.Variable;
import org.homio.api.entity.device.DeviceSeriesEntity;
import org.homio.api.state.DecimalType;
import org.homio.api.ui.field.UIField;
import org.homio.api.ui.field.UIFieldGroup;
import org.homio.api.ui.field.UIFieldNoReadDefaultValue;
import org.homio.api.ui.field.condition.UIFieldShowOnCondition;
import org.homio.api.ui.field.selection.UIFieldVariableSelection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.homio.addon.homekit.HomekitCharacteristicFactory.*;
import static org.homio.addon.homekit.enums.HomekitCharacteristicType.*;
import static org.homio.api.ContextVar.VariableType.*;

// Master class
@SuppressWarnings("unused")
@Entity
@Getter
@Setter
public final class HomekitEndpointEntity extends DeviceSeriesEntity<HomekitEntity> {

    public static final int CURRENT_TEMPERATURE_MIN_CELSIUS = -100;
    private transient int id = -1;

    public static int calculateId(String name) {
        int id = 629 + name.hashCode();
        if (id < 0) {
            id += Integer.MAX_VALUE;
            if (id < 0) id = Integer.MAX_VALUE;
        }
        if (id < 2) {
            id = 2;
        }
        return id;
    }

    public static CurrentTemperatureCharacteristic createCurrentTemperatureCharacteristic(
            @NotNull HomekitEndpointContext c, @NotNull ContextVar.Variable v) {
        return new CurrentTemperatureCharacteristic(
                v.getMinValue(CURRENT_TEMPERATURE_MIN_CELSIUS),
                v.getMaxValue(100),
                v.getStep(0.1),
                getTemperatureSupplier(v, 20.0),
                getSubscriber(v, c, CurrentTemperature),
                getUnsubscriber(v, c, CurrentTemperature));
    }

    @Override
    @UIField(order = 1, required = true)
    @UIFieldGroup(value = "GENERAL", order = 1, borderColor = "#9F48B5")
    public String getName() {
        return super.getName();
    }

    @UIField(order = 5, label = "homekitGroup")
    @UIFieldGroup("GENERAL")
    public String getGroup() {
        return getJsonData("group");
    }

    public void setGroup(String value) {
        setJsonData("group", value);
    }

    // --- Start REQ_CHAR Group ---
    // Note: This group generally starts UI order from 100 internally for HomeKit fields.
    // Fields here are conditionally required based on AccessoryType.

    /**
     * The type of HomeKit accessory this entity represents.
     * Determines which characteristics are available and how the device behaves in HomeKit.
     * Used by: All accessory types (defines the entity's fundamental type).
     */
    @UIField(order = 10, required = true)
    @UIFieldGroup("GENERAL")
    @UIFieldNoReadDefaultValue
    public @NotNull HomekitAccessoryType getAccessoryType() {
        return getJsonDataEnum("at", HomekitAccessoryType.Switch);
    }

    public void setAccessoryType(HomekitAccessoryType value) {
        setJsonDataEnum("at", value);
    }

    @UIField(order = 15)
    @UIFieldGroup("GENERAL")
    @UIFieldNoReadDefaultValue
    public @NotNull String getAsLinkedAccessory() {
        return getJsonData("la");
    }

    public void setAsLinkedAccessory(String value) {
        setJsonData("la", value);
    }

    /**
     * Represents the active state of an accessory.
     * Characteristic: Active (0 = Inactive, 1 = Active).
     * Used by (Required): AirPurifier, Fan, Faucet, HeaterCooler, HumidifierDehumidifier, IrrigationSystem, Television, Valve.
     */
    @UIField(order = 20, required = true)
    @UIFieldShowOnCondition("return ['AirPurifier', 'Fan', 'Faucet', 'HeaterCooler', 'HumidifierDehumidifier', 'Television'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup(value = "REQ_CHAR", order = 100, borderColor = "#8C3265")
    @HomekitCharacteristic(value = StatusActiveCharacteristic.class, type = ActiveStatus)
    public String getStatusActiveState() {
        return getJsonData("sas");
    }

    public void setStatusActiveState(String value) {
        setJsonData("sas", value);
    }

    @UIField(order = 25, required = true)
    @UIFieldShowOnCondition("return ['IrrigationSystem', 'Valve'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = ActiveCharacteristic.class, type = Active)
    public String getActiveState() {
        return getJsonData("as");
    }

    public void setActiveState(String value) {
        setJsonData("as", value);
    }


    /**
     * Represents the detected state for various binary sensors.
     * Characteristic: Varies by sensor (e.g., ContactSensorState, MotionDetected, SmokeDetected, LeakDetected, OccupancyDetected, CarbonMonoxideDetected, CarbonDioxideDetected).
     * Values are typically 0 (Not Detected) or 1 (Detected).
     * Used by (Required): CarbonDioxideSensor, CarbonMonoxideSensor, ContactSensor, LeakSensor, MotionSensor, OccupancySensor, SmokeSensor.
     */
    @UIField(order = 30, required = true)
    @UIFieldShowOnCondition("return ['CarbonDioxideSensor', 'CarbonMonoxideSensor', 'ContactSensor', 'LeakSensor', " +
                            "'MotionSensor', 'OccupancySensor', 'SmokeSensor'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = MotionDetectedCharacteristic.class, type = MotionDetectedState, forAccessory = "MotionSensor")
    @HomekitCharacteristic(value = OccupancyDetectedCharacteristic.class, type = OccupancyDetectedState, forAccessory = "OccupancySensor")
    @HomekitCharacteristic(value = SmokeDetectedCharacteristic.class, type = SmokeDetectedState, forAccessory = "SmokeSensor")
    @HomekitCharacteristic(value = ContactSensorStateCharacteristic.class, type = ContactSensorState,
            forAccessory = "ContactSensor", defaultStringValue = "NOT_DETECTED")
    @HomekitCharacteristic(value = LeakDetectedStateCharacteristic.class, type = LeakDetectedState, forAccessory = "LeakSensor")
    @HomekitCharacteristic(value = CarbonDioxideDetectedCharacteristic.class, type = CarbonDioxideDetectedState, forAccessory = "CarbonDioxideSensor")
    @HomekitCharacteristic(value = CarbonMonoxideDetectedCharacteristic.class, type = CarbonMonoxideDetectedState, forAccessory = "CarbonMonoxideSensor")
    public String getDetectedState() {
        return getJsonData("ds");
    }

    public void setDetectedState(String value) {
        setJsonData("ds", value);
    }

    /**
     * Represents the target position for accessories that can be set to a specific position (e.g., doors, windows, blinds).
     * Characteristic: TargetPosition (0-100%).
     * Used by (Required): Door, Window, WindowCovering. (GarageDoorOpener uses TargetDoorState)
     */
    @UIField(order = 35, required = true)
    @UIFieldShowOnCondition("return ['Door', 'Window', 'WindowCovering'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Percentage)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = TargetPositionCharacteristic.class, type = TargetPosition)
    public String getTargetPosition() {
        return getJsonData("tp");
    }

    public void setTargetPosition(String value) {
        setJsonData("tp", value);
    }

    /**
     * Represents the current position of accessories (e.g., doors, windows, blinds).
     * Characteristic: CurrentPosition (0-100%).
     * Used by (Required): Door, Window, WindowCovering. (GarageDoorOpener uses CurrentDoorState)
     */
    @UIField(order = 40, required = true)
    @UIFieldShowOnCondition("return ['Door', 'Window', 'WindowCovering'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Percentage)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = CurrentPositionCharacteristic.class, type = CurrentPosition)
    public String getCurrentPosition() {
        return getJsonData("cp");
    }

    public void setCurrentPosition(String value) {
        setJsonData("cp", value);
    }

    /**
     * Represents the state of movement for positional accessories.
     * Characteristic: PositionState (0 = Decreasing, 1 = Increasing, 2 = Stopped).
     * Used by (Required): Door, Window, WindowCovering.
     * (GarageDoorOpener uses CurrentDoorState/TargetDoorState, which implies movement).
     */
    @UIField(order = 45, required = true)
    @UIFieldShowOnCondition("return ['Door', 'Window', 'WindowCovering'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = PositionStateCharacteristic.class, type = PositionState)
    public String getPositionState() {
        return getJsonData("ps");
    }

    public void setPositionState(String value) {
        setJsonData("ps", value);
    }

    /**
     * Represents the On/Off state for simple switchable devices.
     * Characteristic: On (true/false or 1/0).
     * Used by (Required): Outlet, Switch, LightBulb, BasicFan.
     */
    @UIField(order = 50, required = true)
    @UIFieldShowOnCondition("return ['Outlet', 'Switch', 'LightBulb', 'BasicFan'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = OnCharacteristic.class, type = OnState, forAccessory = "!Outlet")
    @HomekitCharacteristic(value = OutletInUseCharacteristic.class, type = OnState, forAccessory = "Outlet")
    public String getOnState() {
        return getJsonData("os");
    }

    public void setOnState(String value) {
        setJsonData("os", value);
    }

    /**
     * Represents the mute state for audio output devices.
     * Characteristic: Mute (true/false or 1/0).
     * Used by (Required): Microphone, Speaker, SmartSpeaker, TelevisionSpeaker.
     * Note: Table indicates Mute is optional for SmartSpeaker, but it's often essential for usability. Kept as required here.
     */
    @UIField(order = 55, required = true)
    @UIFieldShowOnCondition("return ['TelevisionSpeaker', 'Speaker', 'Microphone', 'SmartSpeaker'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = MuteCharacteristic.class, type = Mute)
    public String getMute() {
        return getJsonData("mt");
    }

    public void setMute(String value) {
        setJsonData("mt", value);
    }

    /**
     * Represents a programmable switch event.
     * Characteristic: ProgrammableSwitchEvent (0 = Single Press, 1 = Double Press, 2 = Long Press). This is for stateless switches.
     * Used by (Required): Doorbell, StatelessProgrammableSwitch.
     */
    @UIField(order = 60, required = true)
    @UIFieldShowOnCondition("return ['Doorbell', 'StatelessProgrammableSwitch'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Broadcast)
    @UIFieldGroup("REQ_CHAR")
    /*@HomekitCharacteristic(value = ProgrammableSwitchEventCharacteristic.class, type = ProgrammableSwitchEvent,
            impl = ProgrammableSwitchEventCharacteristicSupplier.class)*/
    public String getSingleClickBroadcastEvent() {
        return getJsonData("scbe");
    }

    public void setSingleClickBroadcastEvent(String value) {
        setJsonData("scbe", value);
    }

    @UIField(order = 65)
    @UIFieldShowOnCondition("return ['Doorbell', 'StatelessProgrammableSwitch'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Broadcast)
    @UIFieldGroup("OPT_CHAR")
    public String getDoubleClickBroadcastEvent() {
        return getJsonData("dcbe");
    }

    public void setDoubleClickBroadcastEvent(String value) {
        setJsonData("dcbe", value);
    }

    @UIField(order = 70)
    @UIFieldShowOnCondition("return ['Doorbell', 'StatelessProgrammableSwitch'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Broadcast)
    @UIFieldGroup("OPT_CHAR")
    public String getLongPressBroadcastEvent() {
        return getJsonData("lpbe");
    }

    public void setLongPressBroadcastEvent(String value) {
        setJsonData("lpbe", value);
    }

    /**
     * Represents the current temperature reading.
     * Characteristic: CurrentTemperature (Celsius).
     * Used by (Required): HeaterCooler, TemperatureSensor, Thermostat.
     */
    @UIField(order = 75, required = true)
    @UIFieldShowOnCondition("return ['HeaterCooler', 'TemperatureSensor', 'Thermostat'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Float, requireWritable = false)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = CurrentTemperatureCharacteristic.class, type = CurrentTemperature,
            impl = CurrentTemperatureCharacteristicSupplier.class)
    public String getCurrentTemperature() {
        return getJsonData("ct");
    }

    public void setCurrentTemperature(String value) {
        setJsonData("ct", value);
    }

    /**
     * Represents the target heating/cooling state for climate control devices.
     * Characteristic: TargetHeatingCoolingState (0 = Off, 1 = Heat, 2 = Cool, 3 = Auto).
     * Used by (Required): HeaterCooler, Thermostat.
     */
    @UIField(order = 80, required = true)
    @UIFieldShowOnCondition("return ['HeaterCooler', 'Thermostat'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = TargetHeatingCoolingStateCharacteristic.class, type = TargetHeatingCoolingState,
            defaultStringValue = "OFF", forAccessory = "Thermostat")
    @HomekitCharacteristic(value = TargetHeaterCoolerStateCharacteristic.class, type = TargetHeatingCoolingState,
            defaultStringValue = "AUTO", forAccessory = "HeaterCooler")
    @HomekitValidValues(TargetHeatingCoolingStateEnum.class)
    @HomekitValidValues(TargetHeaterCoolerStateEnum.class)
    public String getTargetHeatingCoolingState() {
        return getJsonData("thcs");
    }

    public void setTargetHeatingCoolingState(String value) {
        setJsonData("thcs", value);
    }

    @UIField(order = 85, required = true)
    @UIFieldShowOnCondition("return ['Thermostat'].includes(context.get('accessoryType'))")
    @UIFieldGroup("REQ_CHAR")
    public Set<TargetHeatingCoolingStateEnum> getTargetHeatingCoolingValidValues() {
        return getJsonDataSet("thcsvv", TargetHeatingCoolingStateEnum.class, TargetHeatingCoolingStateEnum.values());
    }

    public void setTargetHeatingCoolingStateValidValues(String value) {
        setJsonData("thcsvv", value);
    }

    @UIField(order = 90, required = true)
    @UIFieldShowOnCondition("return ['HeaterCooler'].includes(context.get('accessoryType'))")
    @UIFieldGroup("REQ_CHAR")
    public Set<TargetHeaterCoolerStateEnum> getTargetHeaterCoolingValidValues() {
        return getJsonDataSet("thicsvv", TargetHeaterCoolerStateEnum.class, TargetHeaterCoolerStateEnum.values());
    }

    public void setTargetHeaterCoolingValidValues(String value) {
        setJsonData("thicsvv", value);
    }

    /**
     * Represents the current heating/cooling mode of a HeaterCooler.
     * Characteristic: CurrentHeatingCoolingState (0 = Off, 1 = Heat, 2 = Cool).
     * Used by (Required): HeaterCooler. (For Thermostat, this characteristic is optional, see separate field)
     */
    @UIField(order = 95, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'HeaterCooler'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = CurrentHeatingCoolingStateCharacteristic.class, type = CurrentHeatingCoolingState, defaultStringValue = "OFF")
    @HomekitValidValues(CurrentHeatingCoolingStateEnum.class)
    public String getCurrentHeatingCoolingState() {
        return getJsonData("chcs");
    }

    public void setCurrentHeatingCoolingState(String value) {
        setJsonData("chcs", value);
    }

    @UIField(order = 101, required = true)
    @UIFieldShowOnCondition("return ['HeaterCooler'].includes(context.get('accessoryType'))")
    @UIFieldGroup("REQ_CHAR")
    public Set<CurrentHeatingCoolingStateEnum> getCurrentHeatingCoolingValidValues() {
        return getJsonDataSet("chcvv", CurrentHeatingCoolingStateEnum.class, CurrentHeatingCoolingStateEnum.values());
    }

    public void setCurrentHeatingCoolingValidValues(String value) {
        setJsonData("chcvv", value);
    }

    /**
     * Indicates whether the accessory is currently in use.
     * Characteristic: InUse (0 = Not In Uses, 1 = In Use).
     * Used by (Required): Valve, Outlet, IrrigationSystem, Faucet.
     */
    @UIField(order = 106, required = true)
    @UIFieldShowOnCondition("return ['Valve', 'Outlet', 'IrrigationSystem', 'Faucet'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = InUseCharacteristic.class, type = InUseStatus)
    public String getInuseStatus() {
        return getJsonData("ius");
    }

    public void setInuseStatus(String value) {
        setJsonData("ius", value);
    }

    /**
     * Current state of the air purifier.
     * Characteristic: CurrentAirPurifierState (0 = Inactive, 1 = Idle, 2 = Purifying Air).
     * Used by (Required): AirPurifier.
     */
    @UIField(order = 110, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'AirPurifier'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = CurrentAirPurifierCharacteristic.class, type = CurrentAirPurifierState)
    public String getCurrentAirPurifierState() {
        return getJsonData("caps");
    }

    public void setCurrentAirPurifierState(String value) {
        setJsonData("caps", value);
    }

    /**
     * Target state of the air purifier.
     * Characteristic: TargetAirPurifierState (0 = Manual, 1 = Auto).
     * Used by (Required): AirPurifier.
     */
    @UIField(order = 115, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'AirPurifier'")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = TargetAirPurifierStateCharacteristic.class, type = TargetAirPurifierState)
    public String getTargetAirPurifierState() {
        return getJsonData("taps");
    }

    public void setTargetAirPurifierState(String value) {
        setJsonData("taps", value);
    }

    /**
     * Overall air quality level.
     * Characteristic: AirQuality (0 = Unknown, 1 = Excellent, 2 = Good, 3 = Fair, 4 = Inferior, 5 = Poor).
     * Used by (Required): AirQualitySensor.
     */
    @UIField(order = 120, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'AirQualitySensor'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = AirQualityCharacteristic.class, type = AirQuality, defaultStringValue = "UNKNOWN")
    public String getAirQuality() {
        return getJsonData("aq");
    }

    public void setAirQuality(String value) {
        setJsonData("aq", value);
    }

    /**
     * Current battery level.
     * Characteristic: BatteryLevel (percentage 0-100).
     * Used by (Required): Battery.
     */
    @UIField(order = 125, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Battery'")
    @UIFieldVariableSelection(varType = Percentage)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = BatteryLevelCharacteristic.class, type = BatteryLevel)
    public String getBatteryLevel() {
        return getJsonData("bl");
    }

    public void setBatteryLevel(String value) {
        setJsonData("bl", value);
    }

    /**
     * Current charging state of the battery.
     * Characteristic: ChargingState (0 = Not Charging, 1 = Charging, 2 = Not Chargeable).
     * Used by (Required): Battery (especially if chargeable).
     */
    @UIField(order = 130, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Battery'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = ChargingStateCharacteristic.class, type = BatteryChargingState)
    public String getBatteryChargingState() {
        return getJsonData("bcs");
    }

    public void setBatteryChargingState(String value) {
        setJsonData("bcs", value);
    }

    /**
     * Indicates the battery status for the Battery accessory.
     * Characteristic: StatusLowBattery (0 = Normal, 1 = Low).
     * Used by (Required): Battery.
     */
    @UIField(order = 135, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Battery'")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("REQ_CHAR")
    public String getBatteryLowStatus() {
        return getJsonData("slb");
    }

    public void setBatteryLowStatus(String value) {
        setJsonData("slb", value);
    }

    /**
     * Indicates if the filter needs to be changed.
     * Characteristic: FilterChangeIndication (0 = OK, 1 = Change).
     * Used by (Required): Filter. (Optional for AirPurifier, handled by showing in OPT_CHAR for it if also shown there).
     */
    @UIField(order = 140, required = true)
    // Show for Filter (required) and AirPurifier (where it's optional but often present)
    @UIFieldShowOnCondition("['Filter', 'AirPurifier'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = FilterChangeIndicationCharacteristic.class, type = FilterChangeIndication)
    public String getFilterChangeIndication() {
        return getJsonData("fci");
    }

    public void setFilterChangeIndication(String value) {
        setJsonData("fci", value);
    }

    /**
     * Current state of the garage door.
     * Characteristic: CurrentDoorState (0 = Open, 1 = Closed, 2 = Opening, 3 = Closing, 4 = Stopped).
     * Used by (Required): GarageDoorOpener.
     */
    @UIField(order = 145, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'GarageDoorOpener'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = CurrentDoorStateCharacteristic.class, type = CurrentDoorState, defaultStringValue = "CLOSED")
    public String getCurrentDoorState() {
        return getJsonData("cds");
    }

    public void setCurrentDoorState(String value) {
        setJsonData("cds", value);
    }

    /**
     * Target state of the garage door.
     * Characteristic: TargetDoorState (0 = Open, 1 = Closed).
     * Used by (Required): GarageDoorOpener.
     */
    @UIField(order = 150, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'GarageDoorOpener'")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = TargetDoorStateCharacteristic.class, type = TargetDoorState, defaultStringValue = "CLOSED")
    public String getTargetDoorState() {
        return getJsonData("tds");
    }

    public void setTargetDoorState(String value) {
        setJsonData("tds", value);
    }

    /**
     * Current operational state of the heater/cooler. More detailed than 'activeState'.
     * Characteristic: CurrentHeaterCoolerState (0 = Inactive, 1 = Idle, 2 = Heating, 3 = Cooling).
     * Used by (Required): HeaterCooler.
     */
    @UIField(order = 155, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'HeaterCooler'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = CurrentHeaterCoolerStateCharacteristic.class, type = CurrentHeaterCoolerState)
    @HomekitValidValues(CurrentHeaterCoolerStateEnum.class)
    public String getCurrentHeaterCoolerState() {
        return getJsonData("chcshc");
    }

    public void setCurrentHeaterCoolerState(String value) {
        setJsonData("chcshc", value);
    }

    @UIField(order = 160, required = true)
    @UIFieldShowOnCondition("return ['HeaterCooler'].includes(context.get('accessoryType'))")
    @UIFieldGroup("REQ_CHAR")
    public Set<CurrentHeaterCoolerStateEnum> getCurrentHeaterCoolerValidValues() {
        return getJsonDataSet("chicvv", CurrentHeaterCoolerStateEnum.class, CurrentHeaterCoolerStateEnum.values());
    }

    public void setCurrentHeaterCoolerValidValues(String value) {
        setJsonData("chicvv", value);
    }

    /**
     * Current state of the humidifier/dehumidifier.
     * Characteristic: CurrentHumidifierDehumidifierState (0 = Inactive, 1 = Idle, 2 = Humidifying, 3 = Dehumidifying).
     * Used by (Required): HumidifierDehumidifier.
     */
    @UIField(order = 165, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'HumidifierDehumidifier'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    public String getCurrentHumidifierDehumidifierState() {
        return getJsonData("chds");
    }

    public void setCurrentHumidifierDehumidifierState(String value) {
        setJsonData("chds", value);
    }

    /**
     * Target state of the humidifier/dehumidifier.
     * Characteristic: TargetHumidifierDehumidifierState (0 = Auto, 1 = Humidify, 2 = Dehumidify).
     * Used by (Required): HumidifierDehumidifier.
     */
    @UIField(order = 170, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'HumidifierDehumidifier'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = TargetHumidifierDehumidifierStateCharacteristic.class, type = TargetHumidifierDehumidifierState)
    public String getTargetHumidifierDehumidifierState() {
        return getJsonData("thds");
    }

    public void setTargetHumidifierDehumidifierState(String value) {
        setJsonData("thds", value);
    }

    /**
     * Current relative humidity. This is the primary characteristic for a dedicated Humidity Sensor.
     * Characteristic: CurrentRelativeHumidity (percentage 0-100).
     * Used by (Required): HumiditySensor.
     */
    @UIField(order = 175, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'HumiditySensor'")
    @UIFieldVariableSelection(varType = Percentage)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = CurrentRelativeHumidityCharacteristic.class, type = CurrentRelativeHumidity)
    public String getRelativeHumidity() {
        return getJsonData("rh");
    }

    public void setRelativeHumidity(String value) {
        setJsonData("rh", value);
    }

    /**
     * Program mode for irrigation systems.
     * Characteristic: ProgramMode (0 = No Program Scheduled, 1 = Program Scheduled, 2 = Program Scheduled, Manual Mode).
     * Used by (Required): IrrigationSystem.
     */
    @UIField(order = 180, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'IrrigationSystem'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = ProgramModeCharacteristic.class, type = ProgramMode)
    public String getProgramMode() {
        return getJsonData("pm");
    }

    public void setProgramMode(String value) {
        setJsonData("pm", value);
    }

    /**
     * Current ambient light level.
     * Characteristic: CurrentAmbientLightLevel (lux, 0.0001 to 100000).
     * Used by (Required): LightSensor.
     */
    @UIField(order = 185, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'LightSensor'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = CurrentAmbientLightLevelCharacteristic.class, type = LightLevel)
    public String getLightLevel() {
        return getJsonData("ll");
    }

    public void setLightLevel(String value) {
        setJsonData("ll", value);
    }

    /**
     * Current state of the lock.
     * Characteristic: LockCurrentState (0 = Unsecured, 1 = Secured, 2 = Jammed, 3 = Unknown).
     * Used by (Required): Lock.
     */
    @UIField(order = 190, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Lock'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = LockCurrentStateCharacteristic.class, type = LockCurrentState, defaultStringValue = "UNKNOWN")
    public String getLockCurrentState() {
        return getJsonData("lcs");
    }

    public void setLockCurrentState(String value) {
        setJsonData("lcs", value);
    }

    /**
     * Target state of the lock.
     * Characteristic: LockTargetState (0 = Unsecured, 1 = Secured).
     * Used by (Required): Lock.
     */
    @UIField(order = 195, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Lock'")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = LockTargetStateCharacteristic.class, type = LockTargetState)
    public String getLockTargetState() {
        return getJsonData("lts");
    }

    public void setLockTargetState(String value) {
        setJsonData("lts", value);
    }

    /**
     * Current state of the security system.
     * Characteristic: SecuritySystemCurrentState (0 = Stay Arm, 1 = Away Arm, 2 = Night Arm, 3 = Disarmed, 4 = Alarm Triggered).
     * Used by (Required): SecuritySystem.
     */
    @UIField(order = 201, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'SecuritySystem'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = CurrentSecuritySystemStateCharacteristic.class, type = CurrentSecuritySystemState)
    public String getCurrentSecuritySystemState() {
        return getJsonData("sscs");
    }

    public void setCurrentSecuritySystemState(String value) {
        setJsonData("sscs", value);
    }

    /**
     * Target state of the security system.
     * Characteristic: SecuritySystemTargetState (0 = Stay Arm, 1 = Away Arm, 2 = Night Arm, 3 = Disarm).
     * Used by (Required): SecuritySystem.
     */
    @UIField(order = 206, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'SecuritySystem'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = TargetSecuritySystemStateCharacteristic.class, type = TargetSecuritySystemState)
    public String getTargetSecuritySystemState() {
        return getJsonData("ssts");
    }

    public void setTargetSecuritySystemState(String value) {
        setJsonData("ssts", value);
    }

    /**
     * Current state of the slats (e.g., open/closed). This is for a dedicated Slat service, often part of WindowCovering.
     * Characteristic: CurrentSlatState (0 = Fixed, 1 = Jammed, 2 = Swinging).
     * Used by (Required): Slat.
     */
    @UIField(order = 210, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Slat'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = CurrentSlatStateCharacteristic.class, type = CurrentSlatState)
    public String getCurrentSlatState() {
        return getJsonData("css");
    }

    public void setCurrentSlatState(String value) {
        setJsonData("css", value);
    }

    /**
     * Current media state for SmartSpeaker.
     * Characteristic: CurrentMediaState (0=Play, 1=Pause, 2=Stop, 3=Loading, 4=Interrupted – HAP specific values).
     * Used by (Required): SmartSpeaker.
     */
    @UIField(order = 215, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'SmartSpeaker'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = CurrentMediaStateCharacteristic.class, type = CurrentMediaState, defaultStringValue = "UNKNOWN")
    public String getCurrentMediaState() {
        return getJsonData("cms");
    }

    public void setCurrentMediaState(String value) {
        setJsonData("cms", value);
    }

    /**
     * Target media state for SmartSpeaker.
     * Characteristic: TargetMediaState (0=Play, 1=Pause, 2=Stop – HAP specific values).
     * Used by (Required): SmartSpeaker.
     */
    @UIField(order = 220, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'SmartSpeaker'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = TargetMediaStateCharacteristic.class, type = TargetMediaState, defaultStringValue = "STOP")
    public String getTargetMediaState() {
        return getJsonData("tms");
    }

    public void setTargetMediaState(String value) {
        setJsonData("tms", value);
    }

    /**
     * Identifier of the currently active input source on the Television.
     * Characteristic: ActiveIdentifier (UInt32, matches Identifier of an InputSource service).
     * Used by (Required): Television.
     */
    @UIField(order = 225, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Television'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("REQ_CHAR")
    @HomekitCharacteristic(value = ActiveIdentifierCharacteristic.class, type = ActiveIdentifier, defaultIntValue = 1)
    public String getActiveIdentifier() {
        return getJsonData("ai");
    }

    public void setActiveIdentifier(String value) {
        setJsonData("ai", value);
    }

    /**
     * Target temperature for the Thermostat.
     * Characteristic: TargetTemperature (Celsius, typically 10-38).
     * Used by (Required): Thermostat. (One of TargetTemperature, CoolingThresholdTemperature, or HeatingThresholdTemperature must be provided for Thermostat).
     */
    @UIField(order = 230)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Thermostat'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = TargetTemperatureCharacteristic.class, type = TargetTemperature,
            impl = TargetTemperatureCharacteristicSupplier.class)
    public String getTargetTemperature() {
        return getJsonData("tt");
    }

    public void setTargetTemperature(String value) {
        setJsonData("tt", value);
    }

    // --- End REQ_CHAR Group ---


    // --- Start OPT_CHAR Group ---

    /**
     * Type of valve.
     * Characteristic: ValveType (0 = Generic, 1 = Irrigation, 2 = Shower Head, 3 = Water Faucet).
     * Used by (Required): Valve.
     */
    @UIField(order = 235, required = true)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Valve'")
    @UIFieldGroup("REQ_CHAR")
    public ValveTypeEnum getValveType() {
        return getJsonDataEnum("vt", ValveTypeEnum.GENERIC);
    }

    public void setValveType(ValveTypeEnum value) {
        setJsonDataEnum("vt", value);
    }

    /**
     * Indicates the battery status of the accessory.
     * Characteristic: StatusLowBattery (0 = Normal, 1 = Low).
     * Used by (Optional): Many battery-powered sensors. (For Battery accessory type, this is required and handled by a separate field).
     * (AirQualitySensor, CarbonDioxideSensor, CarbonMonoxideSensor, ContactSensor, HumiditySensor, LeakSensor, LightSensor, MotionSensor, OccupancySensor, SmokeSensor, TemperatureSensor).
     */
    @UIField(order = 240)
    @UIFieldShowOnCondition("return ['AirQualitySensor', 'CarbonDioxideSensor', 'CarbonMonoxideSensor', 'ContactSensor', 'HumiditySensor', 'LeakSensor', 'LightSensor', 'MotionSensor', 'OccupancySensor', 'SmokeSensor', 'TemperatureSensor'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup(value = "OPT_CHAR", order = 150, borderColor = "#649639")
    @HomekitCharacteristic(value = StatusLowBatteryCharacteristic.class, type = HomekitCharacteristicType.BatteryLowStatus)
    public String getStatusLowBattery() {
        return getJsonData("slb");
    }

    public void setStatusLowBattery(String value) {
        setJsonData("slb", value);
    }

    @UIField(order = 245)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Battery'")
    @UIFieldVariableSelection(varType = Float, rawInput = true)
    @UIFieldGroup("OPT_CHAR")
    public String getBatteryLowThreshold() {
        return getJsonData("blt", "20");
    }

    public void setBatteryLowThreshold(String value) {
        setJsonData("blt", value);
    }

    /**
     * Indicates if the accessory has been tampered with.
     * Characteristic: StatusTampered (0 = Not Tampered, 1 = Tampered).
     * Used by (Optional): Security-sensitive devices and sensors.
     * (AirQualitySensor, CarbonDioxideSensor, CarbonMonoxideSensor, ContactSensor, HumiditySensor, LeakSensor, LightSensor, MotionSensor, OccupancySensor, SmokeSensor, TemperatureSensor, Window, Door, GarageDoorOpener, SecuritySystem).
     */
    @UIField(order = 250)
    @UIFieldShowOnCondition("return ['SecuritySystem', 'AirQualitySensor', 'CarbonDioxideSensor', 'CarbonMonoxideSensor', 'ContactSensor', 'HumiditySensor', 'LeakSensor', 'LightSensor', 'MotionSensor', 'OccupancySensor', 'SmokeSensor', 'TemperatureSensor', 'Window', 'Door', 'GarageDoorOpener', 'SecuritySystem'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    public String getStatusTampered() {
        return getJsonData("st");
    }

    public void setStatusTampered(String value) {
        setJsonData("st", value);
    }

    /**
     * Indicates a general fault in the accessory.
     * Characteristic: StatusFault (0 = No Fault, 1 = Fault).
     * Used by (Optional): Many sensors and complex devices.
     * (AirQualitySensor, CarbonDioxideSensor, CarbonMonoxideSensor, ContactSensor, HumiditySensor, LeakSensor, LightSensor, MotionSensor, OccupancySensor, SmokeSensor, TemperatureSensor).
     */
    @UIField(order = 255)
    @UIFieldShowOnCondition("return ['AirQualitySensor', 'CarbonDioxideSensor', 'CarbonMonoxideSensor', 'ContactSensor', 'HumiditySensor', 'LeakSensor', 'LightSensor', 'MotionSensor', 'OccupancySensor', 'SmokeSensor', 'TemperatureSensor'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = StatusFaultCharacteristic.class, type = FaultStatus, defaultStringValue = "NO_FAULT")
    public String getFaultStatus() {
        return getJsonData("sf");
    }

    public void setFaultStatus(String value) {
        setJsonData("sf", value);
    }

    /**
     * Swing mode for air purifiers that support oscillation.
     * Characteristic: SwingMode (0 = Disabled, 1 = Enabled).
     * Used by (Optional): AirPurifier.
     */
    @UIField(order = 260)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'AirPurifier'")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    public String getAirPurifierSwingMode() {
        return getJsonData("apsm");
    }

    public void setAirPurifierSwingMode(String value) {
        setJsonData("apsm", value);
    }

    /**
     * Rotation speed for fans or air purifiers.
     * Characteristic: RotationSpeed (percentage 0-100).
     * Used by (Optional): AirPurifier, Fan.
     */
    @UIField(order = 265)
    @UIFieldShowOnCondition("return ['AirPurifier', 'Fan'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Percentage)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = RotationSpeedCharacteristic.class, type = RotationSpeed)
    public String getRotationSpeed() {
        return getJsonData("rs");
    }

    public void setRotationSpeed(String value) {
        setJsonData("rs", value);
    }

    /**
     * Lock physical controls on the device.
     * Characteristic: LockPhysicalControls (0 = Unlocked, 1 = Locked).
     * Used by (Optional): AirPurifier, HeaterCooler, Fan (as LockControl).
     */
    @UIField(order = 270)
    @UIFieldShowOnCondition("return ['AirPurifier', 'HeaterCooler', 'Fan'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = LockPhysicalControlsCharacteristic.class, type = LockControl, defaultStringValue = "CONTROL_LOCK_DISABLED")
    public String getLockPhysicalControls() {
        return getJsonData("lpc");
    }

    public void setLockPhysicalControls(String value) {
        setJsonData("lpc", value);
    }

    /**
     * Ozone density reading.
     * Characteristic: OzoneDensity (ppb, 0-1000).
     * Used by (Optional): AirQualitySensor.
     */
    @UIField(order = 275)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'AirQualitySensor'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = OzoneDensityCharacteristic.class, type = OzoneDensity)
    public String getOzoneDensity() {
        return getJsonData("od");
    }

    public void setOzoneDensity(String value) {
        setJsonData("od", value);
    }

    /**
     * Nitrogen Dioxide density reading.
     * Characteristic: NitrogenDioxideDensity (ppb, 0-1000).
     * Used by (Optional): AirQualitySensor.
     */
    @UIField(order = 280)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'AirQualitySensor'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = NitrogenDioxideDensityCharacteristic.class, type = NitrogenDioxideDensity)
    public String getNitrogenDioxideDensity() {
        return getJsonData("ndd");
    }

    public void setNitrogenDioxideDensity(String value) {
        setJsonData("ndd", value);
    }

    /**
     * Sulphur Dioxide density reading.
     * Characteristic: SulphurDioxideDensity (ppb, 0-1000).
     * Used by (Optional): AirQualitySensor.
     */
    @UIField(order = 285)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'AirQualitySensor'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = SulphurDioxideDensityCharacteristic.class, type = SulphurDioxideDensity)
    public String getSulphurDioxideDensity() {
        return getJsonData("sdd");
    }

    public void setSulphurDioxideDensity(String value) {
        setJsonData("sdd", value);
    }

    /**
     * PM2.5 (Particulate Matter 2.5 micrometers) density reading.
     * Characteristic: PM2_5Density (µg/m³, 0-1000).
     * Used by (Optional): AirQualitySensor.
     */
    @UIField(order = 290)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'AirQualitySensor'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = PM25DensityCharacteristic.class, type = PM25Density)
    public String getPm25Density() {
        return getJsonData("pm25d");
    }

    public void setPm25Density(String value) {
        setJsonData("pm25d", value);
    }

    /**
     * PM10 (Particulate Matter 10 micrometers) density reading.
     * Characteristic: PM10Density (µg/m³, 0-1000).
     * Used by (Optional): AirQualitySensor.
     */
    @UIField(order = 295)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'AirQualitySensor'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = PM10DensityCharacteristic.class, type = PM10Density)
    public String getPm10Density() {
        return getJsonData("pm10d");
    }

    public void setPm10Density(String value) {
        setJsonData("pm10d", value);
    }

    /**
     * VOC (Volatile Organic Compounds) density reading.
     * Characteristic: VOCDensity (µg/m³, 0-1000).
     * Used by (Optional): AirQualitySensor.
     */
    @UIField(order = 300)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'AirQualitySensor'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = VOCDensityCharacteristic.class, type = VOCDensity)
    public String getVocDensity() {
        return getJsonData("vd");
    }

    public void setVocDensity(String value) {
        setJsonData("vd", value);
    }

    /**
     * Current Carbon Dioxide level.
     * Characteristic: CarbonDioxideLevel (ppm, 0-100000).
     * Used by (Optional): CarbonDioxideSensor. ('detectedState' is the required characteristic).
     */
    @UIField(order = 305)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'CarbonDioxideSensor'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = CarbonDioxideLevelCharacteristic.class, type = CarbonDioxideLevel)
    public String getCarbonDioxideLevel() {
        return getJsonData("cdl");
    }

    public void setCarbonDioxideLevel(String value) {
        setJsonData("cdl", value);
    }

    /**
     * Peak Carbon Dioxide level detected.
     * Characteristic: CarbonDioxidePeakLevel (ppm, 0-100000).
     * Used by (Optional): CarbonDioxideSensor.
     */
    @UIField(order = 310)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'CarbonDioxideSensor'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = CarbonDioxidePeakLevelCharacteristic.class, type = CarbonDioxidePeakLevel)
    public String getCarbonDioxidePeakLevel() {
        return getJsonData("cdpl");
    }

    public void setCarbonDioxidePeakLevel(String value) {
        setJsonData("cdpl", value);
    }

    /**
     * Current Carbon Monoxide level.
     * Characteristic: CarbonMonoxideLevel (ppm, 0-100).
     * Used by (Optional): CarbonMonoxideSensor. ('detectedState' is the required characteristic).
     */
    @UIField(order = 315)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'CarbonMonoxideSensor'")
    @UIFieldVariableSelection(varType = Percentage)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = CarbonMonoxideLevelCharacteristic.class, type = CarbonMonoxideLevel)
    public String getCarbonMonoxideLevel() {
        return getJsonData("cml");
    }

    public void setCarbonMonoxideLevel(String value) {
        setJsonData("cml", value);
    }

    /**
     * Peak Carbon Monoxide level detected.
     * Characteristic: CarbonMonoxidePeakLevel (ppm, 0-100).
     * Used by (Optional): CarbonMonoxideSensor.
     */
    @UIField(order = 320)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'CarbonMonoxideSensor'")
    @UIFieldVariableSelection(varType = Percentage)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = CarbonMonoxidePeakLevelCharacteristic.class, type = CarbonMonoxidePeakLevel)
    public String getCarbonMonoxidePeakLevel() {
        return getJsonData("cmpl");
    }

    public void setCarbonMonoxidePeakLevel(String value) {
        setJsonData("cmpl", value);
    }

    /**
     * Indicates if an obstruction is detected during movement.
     * Characteristic: ObstructionDetected (true/false or 1/0).
     * Used by (Optional): Door, Window, WindowCovering, GarageDoorOpener.
     */
    @UIField(order = 325)
    @UIFieldShowOnCondition("return ['Door', 'Window', 'WindowCovering', 'GarageDoorOpener'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = ObstructionDetectedCharacteristic.class, type = ObstructionStatus)
    public String getObstructionDetected() {
        return getJsonData("obd");
    }

    public void setObstructionDetected(String value) {
        setJsonData("obd", value);
    }

    /**
     * Current operational state of the fan, more detailed than just Active.
     * Characteristic: CurrentFanState (0 = Inactive, 1 = Idle, 2 = Blowing Air).
     * Used by (Optional): Fan. 'activeState' is required.
     */
    @UIField(order = 330)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Fan'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = CurrentFanStateCharacteristic.class, type = CurrentFanState, defaultStringValue = "INACTIVE")
    public String getCurrentFanState() {
        return getJsonData("cfs");
    }

    public void setCurrentFanState(String value) {
        setJsonData("cfs", value);
    }

    /**
     * Rotation direction for fans.
     * Characteristic: RotationDirection (0 = Clockwise, 1 = Counter-Clockwise).
     * Used by (Optional): Fan, BasicFan.
     */
    @UIField(order = 335)
    @UIFieldShowOnCondition("return ['Fan', 'BasicFan'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = RotationDirectionCharacteristic.class, type = RotationDirection, defaultStringValue = "CLOCKWISE")
    public String getRotationDirection() {
        return getJsonData("rd");
    }

    public void setRotationDirection(String value) {
        setJsonData("rd", value);
    }

    /**
     * Target fan state (Manual/Auto).
     * Characteristic: TargetFanState (0 = Manual, 1 = Auto).
     * Used by (Optional): Fan.
     */
    @UIField(order = 340)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Fan'")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = TargetFanStateCharacteristic.class, type = TargetFanState, defaultStringValue = "MANUAL")
    public String getTargetFanState() {
        return getJsonData("tfs");
    }

    public void setTargetFanState(String value) {
        setJsonData("tfs", value);
    }

    /**
     * Swing mode for fans that support oscillation.
     * Characteristic: SwingMode (0 = Swing Disabled, 1 = Swing Enabled). (Same as AirPurifier's SwingMode).
     * Used by (Optional): Fan.
     */
    @UIField(order = 345)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Fan'")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = SwingModeCharacteristic.class, type = SwingMode, defaultStringValue = "SWING_DISABLED")
    public String getFanSwingMode() {
        return getJsonData("fsm");
    }

    public void setFanSwingMode(String value) {
        setJsonData("fsm", value);
    }

    /**
     * Remaining life of the filter.
     * Characteristic: FilterLifeLevel (percentage 0-100).
     * Used by (Optional): FilterMaintenance, AirPurifier.
     */
    @UIField(order = 350)
    @UIFieldShowOnCondition("return ['FilterMaintenance', 'AirPurifier'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Percentage)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = FilterLifeLevelCharacteristic.class, type = FilterLifeLevel, defaultIntValue = 100)
    public String getFilterLifeLevel() {
        return getJsonData("fll");
    }

    public void setFilterLifeLevel(String value) {
        setJsonData("fll", value);
    }

    /**
     * Command to reset the filter change indication / life level. Write-only.
     * Characteristic: ResetFilterIndication (Write a value, typically 1, to reset).
     * Used by (Optional): FilterMaintenance, AirPurifier.
     */
    @UIField(order = 355)
    @UIFieldShowOnCondition("return ['FilterMaintenance', 'AirPurifier'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = ResetFilterIndicationCharacteristic.class, type = FilterResetIndication)
    public String getFilterResetIndication() {
        return getJsonData("fri");
    }

    public void setFilterResetIndication(String value) {
        setJsonData("fri", value);
    }

    /**
     * Current state of the garage door's lock mechanism (if present).
     * Characteristic: LockCurrentState (0 = Unsecured, 1 = Secured, 2 = Jammed, 3 = Unknown).
     * Used by (Optional): GarageDoorOpener.
     */
    @UIField(order = 360)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'GarageDoorOpener'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = LockCurrentStateCharacteristic.class, type = LockCurrentState, defaultStringValue = "UNKNOWN")
    public String getLockCurrentStateGarage() {
        return getJsonData("lcsg");
    }

    public void setLockCurrentStateGarage(String value) {
        setJsonData("lcsg", value);
    }

    /**
     * Target state of the garage door's lock mechanism (if present).
     * Characteristic: LockTargetState (0 = Unsecured, 1 = Secured).
     * Used by (Optional): GarageDoorOpener.
     */
    @UIField(order = 365)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'GarageDoorOpener'")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = LockTargetStateCharacteristic.class, type = LockTargetState, defaultStringValue = "UNSECURED")
    public String getLockTargetStateGarage() {
        return getJsonData("ltsg");
    }

    public void setLockTargetStateGarage(String value) {
        setJsonData("ltsg", value);
    }

    /**
     * Cooling threshold temperature for HeaterCooler in Auto mode.
     * Characteristic: CoolingThresholdTemperature (Celsius, typically 10-35).
     * Used by (Optional): HeaterCooler.
     */
    @UIField(order = 370)
    @UIFieldShowOnCondition("return ['Thermostat', 'HeaterCooler'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = CoolingThresholdTemperatureCharacteristic.class, type = CoolingThresholdTemperature,
            impl = CoolingThresholdTemperatureCharacteristicSupplier.class)
    public String getCoolingThresholdTemperature() {
        return getJsonData("ctt");
    }

    public void setCoolingThresholdTemperature(String value) {
        setJsonData("ctt", value);
    }

    /**
     * Heating threshold temperature for HeaterCooler in Auto mode.
     * Characteristic: HeatingThresholdTemperature (Celsius, typically 0-25).
     * Used by (Optional): HeaterCooler.
     */
    @UIField(order = 375)
    @UIFieldShowOnCondition("return ['Thermostat', 'HeaterCooler'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = HeatingThresholdTemperatureCharacteristic.class, type = HeatingThresholdTemperature,
            impl = HeatingThresholdTemperatureCharacteristicSupplier.class)
    public String getHeatingThresholdTemperature() {
        return getJsonData("htt");
    }

    public void setHeatingThresholdTemperature(String value) {
        setJsonData("htt", value);
    }

    @UIField(order = 380)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Thermostat'")
    @UIFieldGroup("OPT_CHAR")
    public TemperatureDisplayUnitEnum getTemperatureUnit() {
        return getJsonDataEnum("tu", TemperatureDisplayUnitEnum.CELSIUS);
    }

    public void setTemperatureUnit(String value) {
        setJsonData("tu", value);
    }

    /**
     * Current relative humidity can be part of a HeaterCooler or HumidifierDehumidifier.
     * Characteristic: CurrentRelativeHumidity (percentage 0-100).
     * Used by (Optional): HeaterCooler, HumidifierDehumidifier.
     */
    @UIField(order = 385)
    @UIFieldShowOnCondition("return ['HeaterCooler', 'HumidifierDehumidifier'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Percentage)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = CurrentRelativeHumidityCharacteristic.class, type = CurrentRelativeHumidity)
    public String getCurrentRelativeHumidity() {
        return getJsonData("crh");
    }

    public void setCurrentRelativeHumidity(String value) {
        setJsonData("crh", value);
    }

    /**
     * Target relative humidity for devices with humidification/dehumidification capabilities.
     * Characteristic: TargetRelativeHumidity (percentage 0-100).
     * Used by (Optional): HeaterCooler (if it has a humidifier function), HumidifierDehumidifier.
     */
    @UIField(order = 390)
    @UIFieldShowOnCondition("return ['Thermostat', 'HeaterCooler', 'HumidifierDehumidifier'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Percentage)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = TargetRelativeHumidityCharacteristic.class, type = TargetRelativeHumidity, defaultDoubleValue = 45.0F)
    public String getTargetRelativeHumidity() {
        return getJsonData("trh");
    }

    public void setTargetRelativeHumidity(String value) {
        setJsonData("trh", value);
    }

    /**
     * Relative humidity threshold for dehumidifier activation (when in Auto mode).
     * Characteristic: RelativeHumidityDehumidifierThreshold (percentage 0-100).
     * Used by (Optional): HumidifierDehumidifier.
     */
    @UIField(order = 395)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'HumidifierDehumidifier'")
    @UIFieldVariableSelection(varType = Percentage, rawInput = true)
    @UIFieldGroup("REQ_CHAR")
    public String getRelativeHumidityDehumidifierThreshold() {
        return getJsonData("rhdt");
    }

    public void setRelativeHumidityDehumidifierThreshold(String value) {
        setJsonData("rhdt", value);
    }

    /**
     * Relative humidity threshold for humidifier activation (when in Auto mode).
     * Characteristic: RelativeHumidityHumidifierThreshold (percentage 0-100).
     * Used by (Optional): HumidifierDehumidifier.
     */
    @UIField(order = 400)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'HumidifierDehumidifier'")
    @UIFieldVariableSelection(varType = Percentage, rawInput = true)
    @UIFieldGroup("REQ_CHAR")
    public String getRelativeHumidityHumidifierThreshold() {
        return getJsonData("rhht");
    }

    public void setRelativeHumidityHumidifierThreshold(String value) {
        setJsonData("rhht", value);
    }

    /**
     * Water level in devices that use water.
     * Characteristic: WaterLevel (percentage 0-100).
     * Used by (Optional): HumidifierDehumidifier, Faucet, Valve (e.g., for a tank).
     */
    @UIField(order = 405)
    @UIFieldShowOnCondition("return ['Valve', 'Faucet', 'HumidifierDehumidifier'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Percentage)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = WaterLavelCharacteristic.class, type = WaterLevel)
    public String getWaterLevel() {
        return getJsonData("wl");
    }

    public void setWaterLevel(String value) {
        setJsonData("wl", value);
    }

    /**
     * Remaining duration for an active irrigation cycle or valve operation.
     * Characteristic: RemainingDuration (seconds). Read-only.
     * Used by (Optional): IrrigationSystem, Valve.
     */
    @UIField(order = 410)
    @UIFieldShowOnCondition("return ['IrrigationSystem', 'Valve'].includes(context.get('accessoryType')) && !context.get('configurationTimer')")
    @UIFieldVariableSelection(varType = Float, requireWritable = false)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = RemainingDurationCharacteristic.class, type = RemainingDuration)
    public String getRemainingDuration() {
        return getJsonData("rdur");
    }

    public void setRemainingDuration(String value) {
        setJsonData("rdur", value);
    }

    @UIField(order = 415)
    @UIFieldShowOnCondition("return ['Valve'].includes(context.get('accessoryType'))")
    @UIFieldGroup("OPT_CHAR")
    public boolean isConfigurationTimer() {
        return getJsonData("cnft", true);
    }

    public void setConfigurationTimer(boolean value) {
        setJsonData("cnft", value);
    }

    /**
     * Set the duration for a valve's operation.
     * Characteristic: SetDuration (seconds). Writeable.
     * Used by (Optional): IrrigationSystem (for individual zones/valves), Valve. (Table uses "Duration" for Valve)
     */
    @UIField(order = 420)
    @UIFieldShowOnCondition("return ['IrrigationSystem', 'Valve'].includes(context.get('accessoryType'))")
    @UIFieldGroup("OPT_CHAR")
    @UIFieldVariableSelection(varType = Float)
    @HomekitCharacteristic(value = SetDurationCharacteristic.class, type = Duration)
    public String getSetDuration() {
        return getJsonData("sdur");
    }

    public void setSetDuration(String value) {
        setJsonData("sdur", value);
    }

    /**
     * Brightness of the light.
     * Characteristic: Brightness (percentage 0-100).
     * Used by (Optional): LightBulb. 'onState' is required.
     */
    @UIField(order = 425)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'LightBulb'")
    @UIFieldVariableSelection(varType = Percentage)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = BrightnessCharacteristic.class, type = Brightness, defaultIntValue = 100)
    public String getBrightness() {
        return getJsonData("br");
    }

    public void setBrightness(String value) {
        setJsonData("br", value);
    }


    /**
     * Hue of the light color.
     * Characteristic: Hue (degrees 0-360).
     * Used by (Optional): LightBulb (for color lights).
     */
    @UIField(order = 430)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'LightBulb'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = HueCharacteristic.class, type = Hue)
    public String getHue() {
        return getJsonData("hu");
    }

    public void setHue(String value) {
        setJsonData("hu", value);
    }

    /**
     * Saturation of the light color.
     * Characteristic: Saturation (percentage 0-100).
     * Used by (Optional): LightBulb (for color lights).
     */
    @UIField(order = 435)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'LightBulb'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = SaturationCharacteristic.class, type = Saturation)
    public String getSaturation() {
        return getJsonData("sa");
    }

    public void setSaturation(String value) {
        setJsonData("sa", value);
    }

    /**
     * Color temperature of the light.
     * Characteristic: ColorTemperature (Mireds, typically 50-400).
     * Used by (Optional): LightBulb (for tunable white lights).
     */
    @UIField(order = 440)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'LightBulb'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = ColorTemperatureCharacteristic.class, type = ColorTemperature,
            impl = ColorTemperatureCharacteristicSupplier.class)
    public String getColorTemperature() {
        return getJsonData("ctemp");
    }

    public void setColorTemperature(String value) {
        setJsonData("ctemp", value);
    }

    /**
     * Hue of the light color.
     * Characteristic: Hue (degrees 0-360).
     * Used by (Optional): LightBulb (for color lights).
     */
    @UIField(order = 445)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'LightBulb'")
    @UIFieldVariableSelection(varType = Color)
    @UIFieldGroup(value = "OPT2_CHAR", borderColor = "#3ABA4F", order = 155)
    public String getCombinedColor() {
        return getJsonData("cmdc");
    }

    public void setCombinedColor(String value) {
        setJsonData("cmdc", value);
    }

    /**
     * Type of alarm for the security system. Read-only.
     * Characteristic: SecuritySystemAlarmType (0 = No Alarm, 1 = Unknown).
     * Used by (Optional): SecuritySystem.
     */
    @UIField(order = 450)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'SecuritySystem'")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = SecuritySystemAlarmTypeCharacteristic.class, type = SecuritySystemAlarmType, defaultStringValue = "UNKNOWN")
    public String getSecuritySystemAlarmType() {
        return getJsonData("ssat");
    }

    public void setSecuritySystemAlarmType(String value) {
        setJsonData("ssat", value);
    }

    /**
     * Type of slats (horizontal or vertical).
     * Characteristic: SlatType (0 = Horizontal, 1 = Vertical).
     * Used by (Optional): Slat. (This is often a configuration rather than dynamic characteristic)
     */
    @UIField(order = 455)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Slat'")
    @UIFieldGroup("OPT_CHAR")
    public SlatTypeEnum getSlatType() {
        return getJsonDataEnum("stype", SlatTypeEnum.HORIZONTAL);
    }

    public void setSlatType(SlatTypeEnum value) {
        setJsonDataEnum("stype", value);
    }

    /**
     * Current tilt angle of slats (part of Slat service or WindowCovering).
     * Characteristic: CurrentTiltAngle (degrees, -90 to 90).
     * Used by (Optional): Slat, WindowCovering (if it has tiltable slats).
     */
    @UIField(order = 460)
    @UIFieldShowOnCondition("return ['Slat', 'WindowCovering'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = CurrentTiltAngleCharacteristic.class, type = CurrentTiltAngle)
    public String getCurrentTiltAngle() {
        return getJsonData("cta");
    }

    public void setCurrentTiltAngle(String value) {
        setJsonData("cta", value);
    }

    /**
     * Target tilt angle of slats (part of Slat service or WindowCovering).
     * Characteristic: TargetTiltAngle (degrees, -90 to 90).
     * Used by (Optional): Slat, WindowCovering (if it has tiltable slats).
     */
    @UIField(order = 465)
    @UIFieldShowOnCondition("return ['Slat', 'WindowCovering'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = TargetTiltAngleCharacteristic.class, type = TargetTiltAngle)
    public String getTargetTiltAngle() {
        return getJsonData("tta");
    }

    public void setTargetTiltAngle(String value) {
        setJsonData("tta", value);
    }

    /**
     * Volume level for audio output.
     * Characteristic: Volume (percentage 0-100).
     * Used by (Optional): SmartSpeaker, Speaker, TelevisionSpeaker, Microphone, Doorbell. 'mute' is required.
     */
    @UIField(order = 470)
    @UIFieldShowOnCondition("return ['SmartSpeaker', 'Speaker', 'TelevisionSpeaker', 'Microphone', 'Doorbell'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Percentage)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = VolumeCharacteristic.class, type = Volume, defaultIntValue = 50)
    public String getVolume() {
        return getJsonData("vol");
    }

    public void setVolume(String value) {
        setJsonData("vol", value);
    }

    /**
     * Name of the input source as configured by the user (e.g., "HDMI 1", "Netflix").
     * Characteristic: ConfiguredName (String). This is for the InputSource service linked to Television.
     * This field would typically be on an InputSource entity, but placed here if TV manages input names directly.
     * Used by (Optional): Television (via linked InputSource services), SmartSpeaker.
     */
    @UIField(order = 475)
    @UIFieldShowOnCondition("return ['Television', 'SmartSpeaker'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Text, rawInput = true)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = ConfiguredNameCharacteristic.class, type = ConfiguredName, defaultStringValue = "Input")
    public String getConfiguredName() {
        return getJsonData("cn");
    }

    public void setConfiguredName(String value) {
        setJsonData("cn", value);
    }

    /**
     * Input Device Type for Television InputSource.
     * Characteristic: InputDeviceType (String enum).
     * Used by (Optional): Television (via linked InputSource services).
     */
    @UIField(order = 480)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Television'")
    @UIFieldVariableSelection(varType = Text, rawInput = true)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = InputDeviceTypeCharacteristic.class, type = InputDeviceType, defaultStringValue = "OTHER")
    public String getInputDeviceType() {
        return getJsonData("idt");
    }

    public void setInputDeviceType(String value) {
        setJsonData("idt", value);
    }

    /**
     * Input Source Type for Television InputSource.
     * Characteristic: InputSourceType (String enum).
     * Used by (Optional): Television (via linked InputSource services).
     */
    @UIField(order = 485)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Television'")
    @UIFieldVariableSelection(varType = Text, rawInput = true)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = InputSourceTypeCharacteristic.class, type = InputSourceType, defaultStringValue = "OTHER")
    public String getInputSourceType() {
        return getJsonData("ist");
    }

    public void setInputSourceType(String value) {
        setJsonData("ist", value);
    }

    /**
     * Sleep/wake discovery mode for the Television.
     * Characteristic: SleepDiscoveryMode (0 = Not Discoverable, 1 = Always Discoverable).
     * Used by (Optional): Television.
     */
    @UIField(order = 490)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Television'")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = SleepDiscoveryModeCharacteristic.class, type = SleepDiscoveryMode, defaultStringValue = "ALWAYS_DISCOVERABLE")
    public String getSleepDiscoveryMode() {
        return getJsonData("sdm");
    }

    public void setSleepDiscoveryMode(String value) {
        setJsonData("sdm", value);
    }

    /**
     * Simulates a remote key press for the Television. Write-only.
     * Characteristic: RemoteKey (various enum values like PlayPause, ArrowUp, Select, etc.).
     * Used by (Optional): Television.
     */
    @UIField(order = 495)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Television'")
    @UIFieldVariableSelection(varType = Text)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = RemoteKeyCharacteristic.class, type = RemoteKey)
    public String getRemoteKey() {
        return getJsonData("rk");
    }

    public void setRemoteKey(String value) {
        setJsonData("rk", value);
    }

    /**
     * Selects if a power mode change should trigger OSD display. Write-only.
     * Characteristic: PowerModeSelection (0=Show, 1=Hide). (Table calls it PowerMode for TV menu)
     * Used by (Optional): Television.
     */
    @UIField(order = 500)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Television'")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = PowerModeCharacteristic.class, type = PowerMode)
    public String getPowerModeSelection() {
        return getJsonData("pmsel");
    }

    public void setPowerModeSelection(String value) {
        setJsonData("pmsel", value);
    }

    /**
     * Closed captions state for Television.
     * Characteristic: ClosedCaptions (0 = Disabled, 1 = Enabled).
     * Used by (Optional): Television.
     */
    @UIField(order = 505)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Television'")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = ClosedCaptionsCharacteristic.class, type = ClosedCaptions, defaultStringValue = "DISABLED")
    public String getClosedCaptions() {
        return getJsonData("cc");
    }

    public void setClosedCaptions(String value) {
        setJsonData("cc", value);
    }

    /**
     * Type of volume control supported by Television or TelevisionSpeaker.
     * Characteristic: VolumeControlType (0 = None, 1 = Relative, 2 = Absolute, 3 = Relative with Current).
     * Used by (Optional): Television, TelevisionSpeaker.
     */
    @UIField(order = 510)
    @UIFieldShowOnCondition("return ['Television', 'TelevisionSpeaker'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = VolumeControlTypeCharacteristic.class, type = VolumeControlType, defaultStringValue = "NONE")
    public String getVolumeControlType() {
        return getJsonData("vct");
    }

    public void setVolumeControlType(String value) {
        setJsonData("vct", value);
    }

    /**
     * Sends volume up/down commands for relative volume control. Write-only.
     * Characteristic: VolumeSelector (0 = Increments, 1 = Decrement).
     * Used by (Optional): Television, TelevisionSpeaker (if VolumeControlType is Relative).
     */
    @UIField(order = 515)
    @UIFieldShowOnCondition("return ['Television', 'TelevisionSpeaker'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = VolumeSelectorCharacteristic.class, type = VolumeSelector)
    public String getVolumeSelector() {
        return getJsonData("vs");
    }

    public void setVolumeSelector(String value) {
        setJsonData("vs", value);
    }

    /**
     * Picture mode for Television.
     * Characteristic: PictureMode (String enum).
     * Used by (Optional): Television.
     */
    @UIField(order = 520)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Television'")
    @UIFieldVariableSelection(varType = Text)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = PictureModeCharacteristic.class, type = PictureMode, defaultStringValue = "OTHER")
    public String getPictureMode() {
        return getJsonData("pmode");
    }

    public void setPictureMode(String value) {
        setJsonData("pmode", value);
    }

    /**
     * Target Visibility State for Television InputSource.
     * Characteristic: TargetVisibilityState (0 = Shown, 1 = Hidden).
     * Used by (Optional): Television (via linked InputSource services).
     */
    @UIField(order = 525)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Television'") // Relates to InputSource
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = TargetVisibilityStateCharacteristic.class, type = TargetVisibilityState, defaultStringValue = "SHOWN")
    public String getTargetVisibilityState() {
        return getJsonData("tvs");
    }

    public void setTargetVisibilityState(String value) {
        setJsonData("tvs", value);
    }

    /**
     * Current relative humidity, if the Thermostat includes a humidity sensor.
     * Characteristic: CurrentRelativeHumidity (percentage 0-100).
     * Used by (Optional): Thermostat.
     */
    @UIField(order = 530)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Thermostat'")
    @UIFieldVariableSelection(varType = Percentage)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = CurrentRelativeHumidityCharacteristic.class, type = RelativeHumidity)
    public String getCurrentRelativeHumidityThermo() {
        return getJsonData("crht");
    }

    public void setCurrentRelativeHumidityThermo(String value) {
        setJsonData("crht", value);
    }

    /**
     * Represents the current heating/cooling mode of a Thermostat.
     * Characteristic: CurrentHeatingCoolingState (0 = Off, 1 = Heat, 2 = Cool).
     * Used by (Optional): Thermostat. (Required for HeaterCooler, handled by a separate field for that)
     */
    @UIField(order = 535)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Thermostat'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = CurrentHeatingCoolingStateCharacteristic.class,
            type = CurrentHeatingCoolingState, defaultStringValue = "OFF")
    public String getCurrentHeatingCoolingStateForThermostat() {
        return getJsonData("chcs");
    }

    public void setCurrentHeatingCoolingStateForThermostat(String value) {
        setJsonData("chcs", value);
    }

    /**
     * Indicates if the valve is configured and ready for use. Read-only.
     * Characteristic: IsConfigured (0 = Not Configured, 1 = Configured).
     * Used by (Optional): Valve.
     */
    @UIField(order = 540)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Valve'")
    @UIFieldVariableSelection(varType = Bool)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = IsConfiguredCharacteristic.class, type = IsConfigured)
    public String getIsConfigured() {
        return getJsonData("ic");
    }

    public void setIsConfigured(String value) {
        setJsonData("ic", value);
    }

    /**
     * Index for a labeled service, e.g., if there are multiple valves in an irrigation system.
     * Characteristic: ServiceLabelIndex (UInt8).
     * Used by (Optional): Valve (especially when grouped, like in IrrigationSystem).
     */
    @UIField(order = 545)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'Valve'")
    @UIFieldGroup("OPT_CHAR")
    public int getServiceIndex() {
        return getJsonData("sli", 0);
    }

    public void setServiceIndex(int value) {
        setJsonData("sli", value);
    }

    /**
     * Command to hold the current position of the window covering. Write-only.
     * Characteristic: HoldPosition (Write any value to hold).
     * Used by (Optional): WindowCovering, Door, Window.
     */
    @UIField(order = 550)
    @UIFieldShowOnCondition("return ['WindowCovering', 'Door', 'Window'].includes(context.get('accessoryType'))")
    @UIFieldVariableSelection(varType = Float) // Or Bool as it's a trigger
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = HoldPositionCharacteristic.class, type = HoldPosition)
    public String getWindowHoldPosition() { // Renamed to getHoldPosition for broader use
        return getJsonData("whp");
    }

    public void setWindowHoldPosition(String value) { // Renamed
        setJsonData("whp", value);
    }

    /**
     * Current horizontal tilt angle of slats on a window covering.
     * Characteristic: CurrentHorizontalTiltAngle (degrees, -90 to 90).
     * Used by (Optional): WindowCovering.
     */
    @UIField(order = 555)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'WindowCovering'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = CurrentHorizontalTiltAngleCharacteristic.class, type = CurrentHorizontalTiltAngle)
    public String getCurrentHorizontalTiltAngle() {
        return getJsonData("chta");
    }

    public void setCurrentHorizontalTiltAngle(String value) {
        setJsonData("chta", value);
    }

    /**
     * Target horizontal tilt angle of slats on a window covering.
     * Characteristic: TargetHorizontalTiltAngle (degrees, -90 to 90).
     * Used by (Optional): WindowCovering.
     */
    @UIField(order = 560)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'WindowCovering'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = TargetHorizontalTiltAngleCharacteristic.class, type = TargetHorizontalTiltAngle)
    public String getTargetHorizontalTiltAngle() {
        return getJsonData("thta");
    }

    public void setTargetHorizontalTiltAngle(String value) {
        setJsonData("thta", value);
    }

    /**
     * Current vertical tilt angle of slats on a window covering.
     * Characteristic: CurrentVerticalTiltAngle (degrees, -90 to 90).
     * Used by (Optional): WindowCovering.
     */
    @UIField(order = 565)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'WindowCovering'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = CurrentVerticalTiltAngleCharacteristic.class, type = CurrentVerticalTiltAngle)
    public String getCurrentVerticalTiltAngle() {
        return getJsonData("cvta");
    }

    public void setCurrentVerticalTiltAngle(String value) {
        setJsonData("cvta", value);
    }

    // --- End OPT_CHAR Group ---


    // --- Start CMN_CHAR Group (Accessory Information Service) ---
    // Note: This group generally starts UI order from 200 internally for HomeKit fields.

    /**
     * Target vertical tilt angle of slats on a window covering.
     * Characteristic: TargetVerticalTiltAngle (degrees, -90 to 90).
     * Used by (Optional): WindowCovering.
     */
    @UIField(order = 570)
    @UIFieldShowOnCondition("return context.get('accessoryType') == 'WindowCovering'")
    @UIFieldVariableSelection(varType = Float)
    @UIFieldGroup("OPT_CHAR")
    @HomekitCharacteristic(value = TargetVerticalTiltAngleCharacteristic.class, type = TargetVerticalTiltAngle)
    public String getTargetVerticalTiltAngle() {
        return getJsonData("tvta");
    }

    public void setTargetVerticalTiltAngle(String value) {
        setJsonData("tvta", value);
    }

    /**
     * Manufacturer of the accessory.
     * Characteristic: Manufacturer (String). Part of the Accessory Information service.
     * Used by (Optional): All accessory types.
     */
    @UIField(order = 575)
    @UIFieldGroup(value = "CMN_CHAR", order = 200, borderColor = "#395D96")
    public String getManufacturer() {
        return getJsonData("man");
    }

    public void setManufacturer(String value) {
        setJsonData("man", value);
    }

    /**
     * Model of the accessory.
     * Characteristic: Model (String). Part of the Accessory Information service.
     * Used by (Optional): All accessory types.
     */
    @UIField(order = 580)
    @UIFieldGroup("CMN_CHAR")
    public String getModel() {
        return getJsonData("mod");
    }

    public void setModel(String value) {
        setJsonData("mod", value);
    }

    /**
     * Serial number of the accessory.
     * Characteristic: SerialNumber (String). Part of the Accessory Information service.
     * Used by (Optional): All accessory types.
     */
    @UIField(order = 585)
    @UIFieldGroup("CMN_CHAR")
    public String getSerialNumber() {
        return getJsonData("sn", "none");
    }

    public void setSerialNumber(String value) {
        setJsonData("sn", value);
    }

    /**
     * Firmware revision of the accessory.
     * Characteristic: FirmwareRevision (String). Part of the Accessory Information service.
     * Used by (Optional): All accessory types.
     */
    @UIField(order = 590)
    @UIFieldGroup("CMN_CHAR")
    public String getFirmwareRevision() {
        return getJsonData("fr", "1");
    }

    public void setFirmwareRevision(String value) {
        setJsonData("fr", value);
    }

    // --- End CMN_CHAR Group ---

    /**
     * Hardware revision of the accessory.
     * Characteristic: HardwareRevision (String). Part of the Accessory Information service.
     * Used by (Optional): All accessory types.
     */
    @UIField(order = 595)
    @UIFieldGroup("CMN_CHAR")
    public String getHardwareRevision() {
        return getJsonData("hr", "");
    }

    public void setHardwareRevision(String value) {
        setJsonData("hr", value);
    }

    @JsonIgnore
    public int getId() {
        if (id == -1) {
            id = calculateId(getTitle());
        }
        return id;
    }

    @Override
    public String getDefaultName() {
        return "Homekit characteristic";
    }

    // These seem like local configuration options, not direct HomeKit characteristics.
    // They should ideally be in a separate UI group if they are general settings.
    // Keeping them at the end for now.

    @Override
    protected String getSeriesPrefix() {
        return "homekit-acs";
    }

    public boolean getEmulateStopState() {
        return getJsonData("emst", true);
    }

    public void setEmulateStopState(boolean value) {
        setJsonData("emst", value);
    }

    public boolean getEmulateStopSameDirection() {
        return getJsonData("emssd", true);
    }

    public void setEmulateStopSameDirection(boolean value) {
        setJsonData("emssd", value);
    }

    public boolean getSendUpDownForExtents() {
        return getJsonData("sudfe", true);
    }

    public void setSendUpDownForExtents(boolean value) {
        setJsonData("sudfe", value);
    }

    @UIField(order = 600)
    @UIFieldShowOnCondition("return ['ColorTemperature', 'Switch', 'WindowCovering', 'Door', 'Window'].includes(context.get('accessoryType'))")
    @UIFieldGroup("OPT_CHAR")
    public boolean getInverted() {
        return getJsonData("inv", false);
    }

    public void setInverted(boolean value) {
        setJsonData("inv", value);
    }

    public static class ColorTemperatureCharacteristicSupplier implements HomekitCharacteristic.CharacteristicSupplier {
        public static final int COLOR_TEMPERATURE_MIN_MIREDS = 107; // ~9300 K
        public static final int COLOR_TEMPERATURE_MAX_MIREDS = 556; // ~1800 K

        @Override
        public BaseCharacteristic<?> get(HomekitEndpointContext c, ContextVar.Variable v) {
            int minM = (int) v.getMinValue(COLOR_TEMPERATURE_MIN_MIREDS);
            int maxM = (int) v.getMaxValue(COLOR_TEMPERATURE_MAX_MIREDS);
            boolean inv = c.endpoint().getInverted();
            Supplier<CompletableFuture<Integer>> getter = () -> {
                int mV = v.getValue().intValue(minM);
                if (inv) mV = maxM - (mV - minM);
                return completedFuture(mV);
            };
            ExceptionalConsumer<Integer> setter = mVHk -> {
                int sV = mVHk;
                if (inv) sV = maxM - (sV - minM);
                v.set(new DecimalType(sV));
            };
            return new ColorTemperatureCharacteristic(minM, maxM, getter, setter,
                    getSubscriber(v, c, ColorTemperature),
                    getUnsubscriber(v, c, ColorTemperature));
        }
    }

    public static class HeatingThresholdTemperatureCharacteristicSupplier implements HomekitCharacteristic.CharacteristicSupplier {
        @Override
        public BaseCharacteristic<?> get(HomekitEndpointContext c, Variable v) {
            return new HeatingThresholdTemperatureCharacteristic(
                    v.getMinValue(0.0),
                    v.getMaxValue(25.0),
                    v.getStep(0.5),
                    getTemperatureSupplier(v, 18.0),
                    setTemperatureConsumer(c, v),
                    getSubscriber(v, c, HeatingThresholdTemperature),
                    getUnsubscriber(v, c, HeatingThresholdTemperature));
        }
    }

    public static class ProgrammableSwitchEventCharacteristicSupplier implements HomekitCharacteristic.CharacteristicSupplier {
        private @Nullable ProgrammableSwitchEnum lastValue;

        @Override
        public BaseCharacteristic<?> get(HomekitEndpointContext c, Variable v) {
            List<ProgrammableSwitchEnum> validValues = new ArrayList<>();
            validValues.add(ProgrammableSwitchEnum.SINGLE_PRESS);
            Variable dcv = c.getVariable(c.endpoint().getDoubleClickBroadcastEvent());
            if (dcv != null) {
                validValues.add(ProgrammableSwitchEnum.DOUBLE_PRESS);
            }
            Variable lpv = c.getVariable(c.endpoint().getLongPressBroadcastEvent());
            if (lpv != null) {
                validValues.add(ProgrammableSwitchEnum.LONG_PRESS);
            }

            /*v.addListener(listenerKey, newState -> {
                if (newState != null) {
                    try {
                        int eventCode = newState.intValue(-1);
                        ProgrammableSwitchEnum hkEvent = null;
                        if (eventCode == 0)
                            hkEvent = ProgrammableSwitchEnum.SINGLE_PRESS;
                        else if (eventCode == 1)
                            hkEvent = ProgrammableSwitchEnum.DOUBLE_PRESS;
                        else if (eventCode == 2)
                            hkEvent = ProgrammableSwitchEnum.LONG_PRESS;
                        if (hkEvent != null) characteristic.sendEvent(hkEvent);
                    } catch (Exception ex) {
                        log.error("Error processing ProgrammableSwitchEvent for {}: {}", e.getName(), ex.getMessage());
                    }
                }
            });*/
            var switchEnums = validValues.toArray(new ProgrammableSwitchEnum[0]);
            return new ProgrammableSwitchEventCharacteristic(switchEnums,
                    () -> CompletableFuture.completedFuture(lastValue),
                    callback -> {
                        /*getSubscriber(v, c, ProgrammableSwitchEvent, i -> lastValue = ProgrammableSwitchEnum.SINGLE_PRESS);
                        getSubscriber(dcv, c, ProgrammableSwitchEvent, i -> lastValue = ProgrammableSwitchEnum.DOUBLE_PRESS);
                        getSubscriber(lpv, c, ProgrammableSwitchEvent, i -> lastValue = ProgrammableSwitchEnum.LONG_PRESS);*/
                    },
                    () -> {
                        /*getUnsubscriber(v, c, ProgrammableSwitchEvent);
                        getUnsubscriber(dcv, c, ProgrammableSwitchEvent);
                        getUnsubscriber(lpv, c, ProgrammableSwitchEvent);*/
                    });
        }
    }

    public static class CurrentTemperatureCharacteristicSupplier implements HomekitCharacteristic.CharacteristicSupplier {
        @Override
        public BaseCharacteristic<?> get(HomekitEndpointContext c, ContextVar.Variable v) {
            return createCurrentTemperatureCharacteristic(c, v);
        }
    }

    public static class TargetTemperatureCharacteristicSupplier implements HomekitCharacteristic.CharacteristicSupplier {
        @Override
        public BaseCharacteristic<?> get(HomekitEndpointContext c, Variable v) {
            return new TargetTemperatureCharacteristic(
                    v.getMinValue(10.0),
                    v.getMaxValue(38.0),
                    v.getStep(0.5),
                    getTemperatureSupplier(v, 21.0),
                    setTemperatureConsumer(c, v),
                    getSubscriber(v, c, TargetTemperature),
                    getUnsubscriber(v, c, TargetTemperature));
        }
    }

    public static class CoolingThresholdTemperatureCharacteristicSupplier implements HomekitCharacteristic.CharacteristicSupplier {
        @Override
        public BaseCharacteristic<?> get(HomekitEndpointContext c, Variable v) {
            return new CoolingThresholdTemperatureCharacteristic(
                    v.getMinValue(10.0),
                    v.getMaxValue(35.0),
                    v.getStep(0.5),
                    getTemperatureSupplier(v, 25.0),
                    setTemperatureConsumer(c, v),
                    getSubscriber(v, c, CoolingThresholdTemperature),
                    getUnsubscriber(v, c, CoolingThresholdTemperature));
        }
    }
}