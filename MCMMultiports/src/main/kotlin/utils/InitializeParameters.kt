package utils

import storage.models.ParameterCommon
import storage.models.ParameterCommonJson

fun initialize(params: List<ParameterCommon>) {
    logAct("initialize params")
    params.forEachIndexed { index, parameterCommon ->
        when(parameterCommon.name) {
            "comport" -> COM_PORT = parameterCommon.value
            "baudrate" -> BAUD_RATE = parameterCommon.value.toIntOrNull() ?: 115200
            //"is_demo" ->
            "last_operator_id" -> OPERATOR_ID = parameterCommon.value
            "sound_enabled" -> SOUND_ENABLED = parameterCommon.value.toIntOrNull() ?: 1
            "delay_before_chart" -> DELAY_BEFORE_CHART
        }
    }
}