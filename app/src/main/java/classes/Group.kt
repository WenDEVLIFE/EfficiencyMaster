package classes

data class Group(var _groupName:String,
    var _groupDescription:String,
    var _memberSize:String,
    ){

    private val groupName:String
    get() = _groupName

    private val groupDescription:String
    get() = _groupDescription

    private val memberSize:String
    get() = _memberSize

    override fun toString(): String {
        return "Group Name: $groupName, Group Description: $groupDescription , Member Size: $memberSize"
    }

}
