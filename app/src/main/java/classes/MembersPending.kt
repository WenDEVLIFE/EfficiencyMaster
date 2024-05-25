package classes

data class MembersPending(var _username:String,
                          var _groupName:String,
                          var _status:String,
                          var _dateRequest:String,
    ){
    val username:String
    get() = _username

    val groupName:String
    get() = _groupName

    val status:String
    get() = _status

    val dateRequest:String
    get() = _dateRequest

    override fun toString(): String {
        return "MembersPending(username=$_username, groupName=$_groupName, status=$_status, dateRequest=$_dateRequest)"
    }
}
