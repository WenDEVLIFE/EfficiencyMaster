package classes

data class Task(
    var _TaskNamme:String,
    var _TaskDescription:String,

){
    val taskname:String
        get() = _TaskNamme

    val taskdescription:String
        get() = _TaskDescription

    override fun toString(): String {
        return "Taskname: $taskname, TaskDescription: $taskdescription"
    }
}
