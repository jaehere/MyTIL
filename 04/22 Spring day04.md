~#70



view.jsp
버튼생성

이전글다음글
lag: 내 기준 윗컬럼 lag(seq) over(order by seq desc) AS previousseq
        2개 위에          lag(seq,2) over(order by seq desc) AS previousseq

lead: 내 기준 아랫컬럼 lead(seq) over(order by seq desc) AS nextseq

inline view 사용

&readCountPermission=yes


댓글쓰기


다음주엔 트랜잭션 처리도 해야해 왜냐면 댓글써주면 그 사람한테 포인트가 올라가도록. 어드바이스로


java.lang.Object
   java.lang.Throwable
      java.lang.Exception

java.lang.Object
   java.lang.Throwable
      java.lang.Error

Exception 과 Error의 부모  Throwable


트랜잭션처리는 이거 한줄만 적어주면 스프링은 알아서 다 해준다.   오류가 발생되면 롤백을 해주겠다.
@Transactional(propagation=Propagation.REQUIRED, isolation=Isolation.READ_COMMITTED, rollbackFor= {Throwable.class})

root-context.xml에 이게 있어야 한다.
<!-- ==== #16. 트랜잭션 처리를 위해서 아래와 같이 트랜잭션매니저 객체를 bean 으로 등록해야 한다. ==== -->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
  <property name="dataSource" ref="dataSource" />
</bean>
<tx:annotation-driven transaction-manager="transactionManager" />

