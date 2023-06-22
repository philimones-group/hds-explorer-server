/* eslint-disable react/prop-types */
import { useState } from "react"
//import ChartLine from "./ChartLine"

function ChartContainer({i18n, chart, type, controls, label}) {
    const[category, setCategory] = useState('total')
  return (
    <>
        <section className="w-full h-[450px] border border-outline bg-white flex flex-col justify-between items-center px-2 pt-4 rounded-xl shadow-xl">
            <h3 className="text-title-alt font-semibold pb-4">{label}</h3>
            <div className="w-full h-full flex flex-col justify-between items-center">
              {chart}
              {controls &&
              (<ul className={`Buttons mx-auto text-[12px] relative flex flex-nowrap w-fit `}>
                  <li className={`w-[105px] text-center p-1.5 border-x border-t border-b-0 border-outline cursor-pointer hover:border-slate-400 ${category == 'total' ? 'bg-btn-clicked text-white' : 'bg-transparent'}`} title="Display all data" onClick={()=> setCategory('total')}>{i18n['dashboard.table.collected']}</li>
                  <li className={`w-[105px] text-center p-1.5 border-x border-t border-b-0 border-outline cursor-pointer hover:border-slate-400 ${category == 'sync' ? 'bg-btn-clicked text-white' : 'bg-transparent'}`} title="Display synchronized forms" onClick={()=> setCategory('sync')}>{i18n['dashboard.table.synced']}</li>
                  <li className={`w-[105px] text-center p-1.5 border-x border-t border-b-0 border-outline cursor-pointer hover:border-slate-400 ${category == 'pending' ? 'bg-btn-clicked text-white' : 'bg-transparent'}`} title="Display pending forms" onClick={()=> setCategory('pending')}>{i18n['dashboard.table.pending']}</li>
                  <li className={`w-[105px] text-center p-1.5 border-x border-t border-b-0 border-outline cursor-pointer hover:border-slate-400 ${category == 'error' ? 'bg-btn-clicked text-white' : 'bg-transparent'}`} title="Display errors forms" onClick={()=> setCategory('error')}>{i18n['dashboard.table.error']}</li>
                  {type=='pie' ? (<li className="absolute w-full h-full"></li>) : null}
              </ul>)}
            </div>
        </section>
    </> 
  )
}

export default ChartContainer