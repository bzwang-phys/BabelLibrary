using System;
using System.Collections.Generic;
using System.IO;

namespace BooksInfo
{
    public struct MultiTreeNode
    {
        public string name;
        public int numChildren;
        public int level;
        public List<MultiTreeNode> children;
    }

    class MultiTree
    {
        public void CreateTree(string fn)
        {
            MultiTreeNode tempNode;
            string name, child;
            StreamReader sr = new StreamReader(fn);
            string stringLine = string.Empty;
            while ((stringLine = sr.ReadLine()) != null)
            {
                string[] strings = stringLine.Split(' ');
                name = strings[0];
                // handle tempNode........
                // ...........
                tempNode.numChildren = strings.Length - 1;
                for (int i = 0; i < tempNode.numChildren; i++)
                {
                    child = strings[i + 2];
                    tempNode.children.Add(new MultiTreeNode());
                }
            }

            sr.Close();
        }
    }

    class CLC
    {
    }

}
