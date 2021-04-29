object MemberController {
    fun join() {
        MemberRepository.joinMember()
    }
    fun logout(): Int {
        println("로그아웃 됬습니다")
        return 0
    }
    fun login(): Int {
        return MemberRepository.loginMember()
    }
}

