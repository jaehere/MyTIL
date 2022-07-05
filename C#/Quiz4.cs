using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Quiz4
{
    class Program
    {
        class Employee
        {

            private string name;
            private int id;

            public Employee()
            {
                name = "";
                id = 0;
            }
            public Employee(int a, string b)
            {
                name = b;
                id = a;
            }
            public void Print()
            {
                Console.Write(" ID:{0} Name:{1} 급여:{2:c}", id, name, CalculatePay());
            }
            virtual public int CalculatePay()
            {
                return 0;
            }
        }

        class SalariedEmp : Employee
        {
            private int salary;
            public SalariedEmp(int id, string name, int s) : base(id, name)
            {
                salary = s;
            }
            public override int CalculatePay() 
            {
                return salary;
            }
        }
        class HourlyEmp : Employee
        {
            
            private int payPerHour;
            private int hours;

            public HourlyEmp(int id, string name, int ph, int h) : base(id, name)
            {
                payPerHour = ph;
                hours = h;
            }
            public override int CalculatePay() 
            {
                return payPerHour * hours;
            }
            
        }
        static void Main(string[] args)
        {
            const int size = 2;
            Employee[] em = new Employee[size]; 
            em[0] = new SalariedEmp(1, "박찬호", 1000000); 
            em[1] = new HourlyEmp(2, "홍길동", 40, 5000); 
            foreach (Employee e in em)
            {
                e.Print();
                Console.WriteLine();
              
            }
        }
    }
}
