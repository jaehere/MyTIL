using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Day3과제6
{
    // delegate 선언
    delegate void MyDelegate(string s);

    class Program
    {
        public static void Hello(string s)
        {
            Console.WriteLine("Hello," + s);
        }
        public static void Goodbye(string s)
        {
            Console.WriteLine("Goodbye," + s);
        }
        static void Main(string[] args)
        {
            string s="";
            string r;
            MyDelegate d1 = new MyDelegate(Hello);
            d1("A");
            //Hello("A");  // delegate를 사용하여 호출하시오.

            MyDelegate d2 = new MyDelegate(Goodbye);
            d2("B");
            //Goodbye("B");// delegate를 사용하여 호출하시오.
        }
    }
}
