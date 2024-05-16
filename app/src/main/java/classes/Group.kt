package classes

data class Group(var _GroupName:String,
    var _GroupDescription:String,
    var _MemberSize:String,
    ){

    val groupName:String
    get() = _GroupName

    val groupDescription:String
    get() = _GroupDescription

    val memberSize:String
    get() = _MemberSize

    override fun toString(): String {
        return "Group Name: $groupName, Group Description: $groupDescription , Member Size: $memberSize"
    }

}
