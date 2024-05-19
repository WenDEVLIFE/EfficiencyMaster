package classes

data class DoneTask(
    var _TaskNamme:String,
    var _TaskDescription:String,
    var _Status:String,
    var _Completion:String,
    ){
    private val taskname:String
        get() = _TaskNamme

    private val taskdescription:String
        get() = _TaskDescription

    private val status:String
        get() = _Status

    private val completion:String
        get() = _Completion

    override fun toString(): String {
        return "Taskname: $taskname, TaskDescription: $taskdescription, Status: $status, Completion: $completion"
    }
}


