package com.backend.api

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.PrintWriter
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException
import java.sql.Statement
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Iterator
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.net.ssl.HttpsURLConnection
import javax.servlet.ServletException
import javax.servlet.annotation.WebInitParam
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import scala.collection.immutable.TreeMap
import scala.collection.mutable.ArrayBuffer
import java.util.UUID
import Transfer._
import scala.collection.mutable.HashMap
import scala.collection.JavaConverters._
import java.net.InetAddress
import java.awt.image.DataBufferByte
import javax.servlet.annotation.WebFilter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.FilterConfig

object Transfer {
  val parser = new JsonParser
  // map to tuples (password, name, gender), a DB would be used in the same form by any given abstraction
  val miniDB = Map(("alpha" -> ("beta", "female", "cassie")), ("aaa" -> ("cccc", "female", "lola")), ("ffff" -> ("jjjj", "male", "roger")), ("alpha1" -> ("beta1", "male", "john")))

  def auth(user: String, pass: String): Boolean = {
    user.equals("alpha")
  }
}

@WebServlet(description = "Endpoint API", urlPatterns = Array("/Auth"))
class Auth extends HttpServlet {

  protected override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val res = new JsonObject
    res.addProperty("result", "invalid")
    val callback = request.getParameter("callback")
    try {
      if (request.getParameter("payload") != null) {
        val payload = parser.parse(request.getParameter("payload")).getAsJsonObject
        val username = payload.get("username").getAsString
        if (miniDB.contains(username)) {
          val password = payload.get("password").getAsString
          val l = miniDB(username)
          if (l._1.equals(password)) {
            res.addProperty("result", "invalid")
          }
        }
      }
    } catch {
      case e: Exception =>
    }
    response.setContentType("application/json")
    val out = response.getWriter
    out.print(callback + "(" + res + ");")
  }

  protected override def doPost(request: HttpServletRequest, response: HttpServletResponse) {
  }
}

@WebServlet(description = "Endpoint API", urlPatterns = Array("/Filter"))
class Filter extends HttpServlet {

  protected override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val res = new JsonArray
    val callback = request.getParameter("callback")
    try {
      if (request.getParameter("payload") != null) {
        val payload = parser.parse(request.getParameter("payload")).getAsJsonObject
        val filter = payload.get("filter").getAsString
        def compfn(e1: (String, String, String), e2: (String, String, String)) = e1._3.compareTo(e2._3) > 0
        val filtered = miniDB.values.filter(_._2.equals(filter)).toList.sortWith(compfn)
      }
    } catch {
      case e: Exception =>
    }
    response.setContentType("application/json")
    val out = response.getWriter
    out.print(callback + "(" + res + ");")
  }

  protected override def doPost(request: HttpServletRequest, response: HttpServletResponse) {
  }
}

@WebServlet(description = "Endpoint API", urlPatterns = Array("/Status"))
class Status extends HttpServlet {

  protected override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val res = new JsonObject
    val callback = request.getParameter("callback")
    try {
      if (request.getParameter("payload") != null) {
        res.addProperty("result", "invalid")
      }
    } catch {
      case e: Exception =>
    }
    response.setContentType("application/json")
    val out = response.getWriter
    out.print(callback + "(" + res + ");")
  }

  protected override def doPost(request: HttpServletRequest, response: HttpServletResponse) {
  }
}

@WebServlet(description = "Endpoint API", urlPatterns = Array("/ListFiles"))
class ListFiles extends HttpServlet {

  import scala.util.matching.Regex
  def recursiveListFiles(f: File, r: Regex): Array[File] = {
    val these = f.listFiles
    val good = these.filter(f => r.findFirstIn(f.getName).isDefined)
    good ++ these.filter(_.isDirectory).flatMap(recursiveListFiles(_, r))
  }

  protected override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val res = new JsonArray
    val callback = request.getParameter("callback")
    try {
      if (request.getParameter("payload") != null) {
        val payload = parser.parse(request.getParameter("payload")).getAsJsonObject
        val filter = payload.get("regex").getAsString
        val reg = new Regex(filter)
        val f = new File("C:\temp")
        val l = recursiveListFiles(f, reg)
        for (s <- l) {
          val obj = new JsonObject
          obj.addProperty("name", s.getName())
          res.add(obj)
        }
      }
    } catch {
      case e: Exception =>
    }
    response.setContentType("application/json")
    val out = response.getWriter
    out.print(callback + "(" + res + ");")
  }

  protected override def doPost(request: HttpServletRequest, response: HttpServletResponse) {
  }
}