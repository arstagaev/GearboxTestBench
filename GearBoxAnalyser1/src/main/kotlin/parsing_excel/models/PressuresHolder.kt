package parsing_excel.models

data class PressuresHolder(
    val displayName : String,
    val index : Int,
    var minValue : Int,
    var maxValue : Int,
    val tolerance : Int,
    val unit : String,
    val commentString : String,
    val prefferedColor : String,
    var isVisible : Boolean
)
