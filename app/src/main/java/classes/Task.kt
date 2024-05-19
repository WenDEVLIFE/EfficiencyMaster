package classes

data class Task(
    var _taskNamme:String,
    var _taskDescription:String,

){
    private val taskname:String
        get() = _taskNamme

    private val taskdescription:String
        get() = _taskDescription

    override fun toString(): String {
        return "Taskname: $taskname, TaskDescription: $taskdescription"
    }
}
