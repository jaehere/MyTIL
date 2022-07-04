/*

두 정수 n과 m 사이의 짝수의 갯수를 구하는 프로그램을

실행 결과는 다음과 같으며 CountEven이라는 메소드를 정의하여 구현하시오

첫번째 정수를 입력하시오:4
두번째 정수를 입력하시오:9
4와 9 사이의 짝수는 3개 입니다.

int CountEven(int n, int m)
{
  ...
  return 짝수의갯수;
}

*/



using System;

namespace Ch6Ex
{
    class Util
    { 
        public static int CountEven( int aa,  int bb)
        {
            int count = 0;
            if (aa < bb)
            {


                for (int i = aa; i <= bb; i++)
                {

                    if (i % 2 == 0)
                    {
                        count++;
                    }

                }
                
            }
            else
            {
                for (int i = bb; i <= aa; i++)
                {

                    if (i % 2 == 0)
                    {
                        count++;
                    }

                }
            }
            return count;

        }
        
    }

        class Program
    {
        static void Main(string[] args)
        {
            int count = 0;
            Console.Write("첫번째 정수를 입력하시오:");
            string a = Console.ReadLine();
            
            Console.Write("두번째 정수를 입력하시오:");
            string b = Console.ReadLine();

            //int num;

            int aa = int.Parse(a);
            int bb = int.Parse(b);

            count = Util.CountEven(aa, bb);

            Console.WriteLine($"{aa}와 {bb} 사이의 짝수는 {count}개 입니다.");
        }
    }
}
