import { useState } from "react"

/* eslint-disable react/prop-types */
export function Table({i18n, dataList}){
    //State for max number of displayed rows on the table
    const [maxRow, setMaxRow] = useState(5)
    //Function for handle max number of rows change
     const handleMaxRowChange = (e) => {
            setMaxRow(e.target.value)
    }

    return (
        <section className="overflow-auto w-full bg-white shadow-xl border border-outline">
            <table className="w-full table-auto border-collapse my-10 text-sm">
                <thead>
                    <tr>
                        <th>{i18n['dashboard.table.id']}</th>
                        <th>{i18n['dashboard.table.fieldworker']}</th>
                        <th>{i18n['dashboard.table.collected']}</th>
                        <th>{i18n['dashboard.table.synced']}</th>
                        <th>{i18n['dashboard.table.pending']}</th>
                        <th>{i18n['dashboard.table.error']}</th>
                    </tr>
                </thead>
                <tbody>
                    {dataList?.slice(0, maxRow).map((item)=>
                    (<tr key={item.id}>
                        <td>{item.id}</td>
                        <td>{item.name}</td>
                        <td>{item.formCollected}</td>
                        <td>{item.formSynchronized}</td>
                        <td>{item.formPending}</td>
                        <td>{item.formError}</td>
                        <td>
                            <button className="p-1 mx-2 rounded-md w-[60px] bg-blue-300 text-white hover:brightness-90">{i18n['dashboard.table.see']}</button>
                        </td>
                    </tr>)
                    )}
            </tbody>
            <div className="flex items-center gap-2 mt-4 pl-2 text-title">
                <label htmlFor="row">{i18n['dashboard.table.rows']}</label>
                <input className="border border-outline p-1.5 w-16" type="number" name="row" id="1" value={maxRow} onChange={handleMaxRowChange}/>
            </div>
        </table>
    </section>
    )
}