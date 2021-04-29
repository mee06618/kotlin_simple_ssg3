object MemberRepository {
    private val members = mutableListOf<Member>()
    private var lastNum = 0

    fun joinMember(){
        val num= ++lastNum
        print("로그인 아이디 : ")
        val id = readLineTrim()
        print("로그인 비번 : ")
        val pass = readLineTrim()
        print("이름 : ")
        val name= readLineTrim()
        print("별명 : ")
        val nickname = readLineTrim()
        print("휴대전화 번호 : ")
        val phone = readLineTrim()
        print("이메일 : ")
        val email = readLineTrim()
        for(member in members) {
            if (member.id==id) {
                println("${id}는 이미 존재합니다")
                return
            }
        }
        val temp = Member(num,id,pass,name,nickname,phone,email)
        members.add(temp)
        lastNum=num

        println("회원 가입이 완료되었습니다")
    }

    fun loginMember() :Int{
        print("아이디 : ")
        val id = readLineTrim()
        print("비밀번호 : ")
        val pass = readLineTrim()
        for(member in members) {
            if (member.id.contains(id)) {
                if(member.pass == pass){

                    println("${member.nickname}님 환영합니다!")

                    return member.num
                }else{
                    println("틀린 비번입니다")
                }
            } else {
                println("존재하지 않는 id입니다")
            }
        }
        return 0
    }
    fun getNick(num:Int):String{
        for(member in members) {
            if (member.num == num) {
                return member.nickname
            }
        }
        return ""
    }

}