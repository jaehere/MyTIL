using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Divide
{
    class Program
    {
        static void Main(string[] args)
        {
            //과제1)두 정수를 입력받아서 나누기를 하는 프로그램을 작성하시오.
            //실행결과는 아래와 같습니다.
            //첫번째 정수를 입력하시오: 10
            //두번째 정수를 입력하시오: 2
            //10을 2로 나눈 결과는 5입니다.
            Console.Write("첫번째 정수를 입력하시오: ");
            string a = Console.ReadLine();

            Console.Write("두번째 정수를 입력하시오: ");
            string b = Console.ReadLine();
            
            int c;
            c = Convert.ToInt32(a)/Convert.ToInt32(b);

            Console.WriteLine("{0}을 {1}로 나눈 결과는 {2}입니다.", a,b,c);
        }
    }
}
