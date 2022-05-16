package org.philimone.hds.explorer.server.model.collect.raw

import com.google.gson.Gson
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

import java.time.LocalDateTime

class RawErrorLog {

    /*
     * UUID or Id of a Raw Data Collected using Mobile/Web
     */
    String uuid


    LogReportFile logReportFile

    /*
     * The entity that is being executed
     */
    RawEntity entity


    String columnName
    /*
     * Code(human readable code) of the specific entity if exists
     */
    String code
    /*
     * A json based text with the descriptions of the errors and which variables they are affecting
     *
     */
    String message
    /*
     * Timestamp of Execution
     */
    LocalDateTime createdDate = LocalDateTime.now()

    void setMessages(ArrayList<RawMessage> messages){
        Gson gson = new Gson()
        this.message = gson.toJson(messages)
    }

    ArrayList<RawMessage> getMessages(){
        def list = new ArrayList<RawMessage>()
        if (message==null || message.isEmpty()) return list

        Gson gson = new Gson()
        return gson.fromJson(this.message, new ArrayList<RawMessage>().getClass())
    }

    String getMessageText(){
        def list = getMessages()
        def text = ""

        list.each { rmsg ->
            text += (text.isEmpty() ? "" : "\n" )+rmsg.text
        }

        return text

    }

    String getCollapsedMessage(){
        def msg = messageText
        if (msg.length()>=80){
            return msg.substring(0, 80)
        }

        return msg
    }

    static constraints = {
        uuid unique: true, nullable: false, maxSize: 32
        logReportFile nullable: false
        entity nullable: false
        columnName blank: true, nullable: true
        code blank: true, nullable: true
        message blank: false, maxSize: 1000
    }

    static mapping = {
        table "_raw_error_log"

        id column: "uuid", name: "uuid", generator: "assigned"

        logReportFile column: "log_report_file_uuid"

        entity column: "entity", index: "idx_entity"
        columnName column: "column_name"
        code column: "code", index: "idx_code"
        message column: "message"
        createdDate column: "created_date"
    }
}
