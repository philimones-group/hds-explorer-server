/* eslint-disable react/prop-types */
import {BsArrowRepeat} from 'react-icons/bs'
import {AiOutlineCheck, AiOutlineClose} from 'react-icons/ai'
import '../assets/extra.css'

function Card({i18n, label, data, icon, iconBg, iconColor}) {

  //format card values numbers
  const formatNumber = (number) => {
    if (!number || number == null) {
        return "0"
    } else {
        return number.toLocaleString('en-us')
    }

  }

  return (
    <div className='my-6'>
      {/* Tenta fazer essa UL class w-full expandir sozinha, eu aumentei para 270x*/}
      <ul className='w-full md:w-[270px] h-[140px] 2xl:w-[310px] p-4 bg-white rounded-xl shadow-2xl'>
        <li className='flex justify-between mb-2'>
          <span className={`relative flex justify-center items-center w-[65px] h-[65px] text-[28px] rounded-full shadow-xl border ${iconBg} ${iconColor}`}>{icon}</span>
          <div>
            <span className='block text-right text-card-label text-[15px]'>{label}</span>
            <span className='block text-right text-card-value text-2xl'>{!data? <span className='loader'></span> : formatNumber(data?.formCollected)}</span>
          </div>
        </li>
        <li className='border-t pt-4 text-[15px] flex justify-between items-center text-sub-title'>
            <div className='flex items-center gap-1'>
                <span>{formatNumber(data?.formSynchronized)}</span>
                <span className='text-[10px]'>{i18n['dashboard.synced']}</span>
                <AiOutlineCheck className='text-green-600'/>
            </div>
            <div className='flex items-center gap-1'>
                <span>{formatNumber(data?.formPending)}</span>
                <span className='text-[10px]'>{i18n['dashboard.pending']}</span>
                <BsArrowRepeat className='text-yellow-500'/>
            </div>
            <div className='flex items-center gap-1'>
                <span>{formatNumber(data?.formError)}</span>
                <span className='text-[10px]'>{i18n['dashboard.error']}</span>
                <AiOutlineClose className='text-red-600'/>
            </div>
        </li>
      </ul>
    </div>
  )
}

export default Card