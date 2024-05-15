package classes

data class Group(var _GroupName:String,
    var _GroupDescription:String,
    ){

    val groupName:String
    get() = _GroupName

    val groupDescription:String
    get() = _GroupDescription

    override fun toString(): String {
        return "Group Name: $groupName, Group Description: $groupDescription"
    }

}
