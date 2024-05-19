package classes

data class Group(var _groupName:String,
    var _groupDescription:String,
    var _memberSize:String,
    ){

     val groupName:String
    get() = _groupName

    val groupDescription:String
    get() = _groupDescription

     val memberSize:String
    get() = _memberSize

    override fun toString(): String {
        return "Group Name: $groupName, Group Description: $groupDescription , Member Size: $memberSize"
    }

}
