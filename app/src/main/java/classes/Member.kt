package classes

data class Member(
    var _username:String,
    var _role:String,
    var _userid:String,
    var _joinedDate:String,

    ){

    val username:String
    get() = _username

    val role:String
    get() = _role

    val userid:String
    get() = _userid

    val joinedDate:String
    get() = _joinedDate

    override fun toString(): String {
        return "Member(username='$username', role='$role', userid='$userid', joinedDate='$joinedDate')"
    }
}
