using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Day3과제7입니다
{
    class Quiz7
    {
        abstract class Employee
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
            abstract public int CalculatePay();
            //virtual public int CalculatePay()
            //{
            //    return 0;
            //}
        }

        class SalariedEmp : Employee
        {
            protected int salary;
            public SalariedEmp(int id, string name, int s) : base(id, name)
            {
                salary = s;
            }
            public override int CalculatePay() // abstract method 반드시 재정의 해야함.
            {
                return salary;
            }
        }
        class SalesPerson : SalariedEmp
        {
            private int incentive;
            private int sales;
            public SalesPerson(int id, string name, int salary, int incentive, int sales) : base(id, name, salary)
            {
                this.incentive = incentive;
                this.sales = sales;
            }
            public override int CalculatePay() // abstract method 반드시 재정의 해야함.
            {
                return salary + incentive * sales;
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
            public override int CalculatePay() // abstract method 반드시 재정의 해야함.
            {
                return payPerHour * hours;
            }

        }
        static void Main(string[] args)
        {
            const int size = 3;
            Employee[] em = new Employee[size]; // <=======
            em[0] = new SalariedEmp(1, "박찬호", 1000000);  //upcasting
            em[1] = new HourlyEmp(2, "홍길동", 40, 5000);  //upcasting
            em[2] = new SalesPerson(3, "이순신", 1000000, 10, 20000);
            foreach (Employee e in em)
            {
                e.Print();
                Console.WriteLine();

            }
        }
    }
}
