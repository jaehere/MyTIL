using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Ch5Ex4
{
    class Program
    {
        static void Main(string[] args)
        {

            int n;
            do
            {
                Console.Write("반복 횟수를 입력하세요:");

                n = Convert.ToInt32(Console.ReadLine());
                if (n <= 0)
                    Console.WriteLine("0보다 작거나 같은 수는 입력할 수 없습니다.");
                Console.WriteLine();
            }
            while (n <= 0);

            for (int i = 0; i < n; i++)
            {
                for (int j = 0; j <= i; j++)
                {
                    Console.Write("*");
                }
                Console.WriteLine();
            }

        }
    }
}
