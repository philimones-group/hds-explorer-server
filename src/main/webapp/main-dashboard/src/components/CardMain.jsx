import { BsGenderFemale, BsGenderMale } from "react-icons/bs"

/* eslint-disable react/prop-types */
function CardMain({i18n, label, value ,icon, iconBg, genderStatus, maleCount, femaleCount}) {

    console.info("i18n> "+i18n)

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
      <ul className='w-full md:w-[280px] h-[140px] 2xl:w-[280px] p-4 rounded-xl bg-white shadow-2xl'>
        <li className='flex justify-between mb-2'>
          <span className={`relative -top-8 flex justify-center items-center w-[65px] h-[65px] rounded-xl shadow-2xl ${iconBg} text-white`}>{icon}</span>
          <div>
            <span className='block text-right text-[15px] text-title-alt font-light'>{label}</span>
            <span className='block text-right text-2xl text-title-alt font-bold'>{formatNumber(value)}</span>
          </div>
        </li>
        <li className='border-t pt-4 text-[15px] flex gap-1 items-center'>
            <div className={`w-full flex justify-between ${genderStatus == false ? 'hidden' : null}`}>
                <div className="flex gap-2 items-center">
                    <BsGenderMale className="text-costume-blue"/>
                    <span>{formatNumber(maleCount)}</span>
                    <span className="text-sm font-light">{i18n['dashboard.males']}</span>
                </div>
                <div className="flex gap-2 items-center">
                    <BsGenderFemale className="text-costume-pink"/>
                    <span>{formatNumber(femaleCount)}</span>
                    <span className="text-sm font-light">{i18n['dashboard.females']}</span>
                </div>
            </div>
        </li>
      </ul>
    </div>
  )
}

export default CardMain