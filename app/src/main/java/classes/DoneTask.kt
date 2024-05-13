package classes

data class DoneTask(
    var _TaskNamme:String,
    var _TaskDescription:String,
    var _Status:String,
    var _Completion:String,
    ){
    val taskname:String
        get() = _TaskNamme

    val taskdescription:String
        get() = _TaskDescription

    val stauts:String
        get() = _Status

    val completion:String
        get() = _Completion

    override fun toString(): String {
        return "Taskname: $taskname, TaskDescription: $taskdescription"
    }
}
