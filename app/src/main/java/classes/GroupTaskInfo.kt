package classes

data class GroupTaskInfo(var _taskName:String,
                         var _taskDescription:String,
                         var _status:String,
                         var _assigned:String,
                         var _createdBy :String,
){
    val taskname:String
        get() = _taskName

    val taskdescription:String
        get() = _taskDescription

    val status:String
        get() = _status

    val assigned:String
        get() = _assigned

    val createdby:String
        get() = _createdBy

    override fun toString(): String {
        return "Taskname: $taskname, TaskDescription: $taskdescription, Status: $status, Completion: $assigned , CreatedBy: $createdby"
    }
}