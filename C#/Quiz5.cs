//C# (2022-07-04)
//과제5(Day3)

//오후 1:38 (오후 10:07에 수정됨)
//100/100
//100/100점
//다음 클래스에 인덱서(Indexer)를  구현하시오.

﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Day3과제5
{
    

    
    class StringList
    {

        private string[] list;
        //인덱서
        //생성자
        
        public int Count
        {
            get { return list.Length; }
        }
        public string this[int n] //n이 index이다.
        {
            get
            {
                return list[n];
            }
            set
            {
                list[n] = value;
            }
        }
        public StringList(int size)
        {
            list = new string[size];
        }
    }
    
        class Program
    {
        static void Main(string[] args)
        {
            StringList myList = new StringList(5);
            myList[3] = "hello";  // 인덱서   //myList.list[3]="hello"
                
            String myString = myList[3];
            Console.WriteLine(myString);  //hello  

          
        }
    }
    
}
Program.cs
Program.cs 표시 중입니다.
