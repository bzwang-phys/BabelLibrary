using System;
using System.Net;
using System.Text;
using System.IO;
using HtmlAgilityPack;
using System.Collections.Generic;
// using System.Xml.XPath;

namespace BooksInfo
{
    class Scrapy
    {
        public string Uri { get; set; }
        public string HtmlStr { get; set; }

        public Scrapy(string uriString)
        {
            Uri = uriString;
        }

        public string ParseUri()
        {
            return "x";
        }

        public void DoubanHtml()
        {
            HttpWebRequest req = (HttpWebRequest)WebRequest.Create(Uri);
            req.Method = "GET";
            HttpWebResponse resq = (HttpWebResponse)req.GetResponse();

            string htmlCharset = "UTF-8";
            Encoding htmlEncoding = Encoding.GetEncoding(htmlCharset);
            StreamReader sr = new StreamReader(resq.GetResponseStream(), htmlEncoding);

            HtmlStr = sr.ReadToEnd();
            sr.Close();
        }

        public Dictionary<string, string> DoubanInfo()
        {
            Dictionary<string, string> dict = new Dictionary<string, string>();
            DoubanHtml();
            HtmlDocument doc = new HtmlDocument();
            doc.LoadHtml(HtmlStr);
            // find all nodes <span class="pl"> in the <id=info>
            HtmlNodeCollection nodes = doc.DocumentNode.SelectNodes("//div[@id='info']//span[@class='pl']");
            char[] charsToTrim = { ' ', ':' };

            foreach (var node in nodes)
            {
                string key = node.InnerText.Trim(charsToTrim);
                string name;
                HtmlNode nextNode = node.NextSibling;

                if (nextNode.NextSibling.Name == "br")
                {
                    name = nextNode.InnerText.Trim(charsToTrim);
                }
                else if (nextNode.NextSibling.Name == "a")
                {
                    name = nextNode.NextSibling.InnerText.Trim(charsToTrim);
                }
                else
                {
                    name = "Not Found";
                }

                dict.Add(key, name);
            }
            return dict;
        }

        static public void SearchBook(string bookName)
        {

        }

        public void ParseHtmlX()
        {

        }

    }
}