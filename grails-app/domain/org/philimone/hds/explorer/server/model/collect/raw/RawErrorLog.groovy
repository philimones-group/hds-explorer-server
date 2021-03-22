package org.philimone.hds.explorer.server.model.collect.raw

import com.google.gson.Gson
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

class RawErrorLog {

    /*
     * UUID or Id of a Raw Data Collected using Mobile/Web
     */
    String uuid
    /*
     * The entity that is being executed
     */
    RawEntity entity
    /*
     * Code(human readable code) of the specific entity if exists
     */
    String code
    /*
     * A json based text with the descriptions of the errors and which variables they are affecting
     *
     */
    String message

    void setMessage(ArrayList<RawMessage> messages){
        Gson gson = new Gson()
        this.message = gson.toJson(messages)
    }

    ArrayList<RawMessage> getMessage(){
        def list = new ArrayList<RawMessage>()
        if (message==null || message.isEmpty()) return list;

        Gson gson = new Gson()
        return gson.fromJson(this.message, new ArrayList<RawMessage>().getClass())
    }

    static constraints = {
        uuid unique: true, nullable: false
        entity nullable: false
        code blank: true, nullable: true
        message blank: false, maxSize: 1000
    }

    static mapping = {
        table "_raw_error_log"

        id name: "uuid", generator: "assigned"

        entity column: "entity", indexColumn: [name: "idx_entity", type: Integer]
        code column: "code", indexColumn: [name: "idx_code", type: Integer]
        message column: "message"
    }
}
