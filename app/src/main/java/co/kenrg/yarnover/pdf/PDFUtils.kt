package co.kenrg.yarnover.pdf

import java.net.URLConnection

fun isUrlConnectionAPdf(connection: URLConnection) =
    connection.contentType == "application/pdf"
