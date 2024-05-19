package classes

data class Task(
    var _taskNamme:String,
    var _taskDescription:String,

){
    val taskname:String
        get() = _taskNamme

    val taskdescription:String
        get() = _taskDescription

    override fun toString(): String {
        return "Taskname: $taskname, TaskDescription: $taskdescription"
    }
}
