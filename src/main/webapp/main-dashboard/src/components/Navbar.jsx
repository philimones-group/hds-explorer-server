/* eslint-disable react-hooks/exhaustive-deps */
import { HiHome, HiUserCircle } from 'react-icons/hi'
import { HiCog6Tooth, HiPower} from 'react-icons/hi2'
import { useEffect, useState } from 'react'
import { useLocation } from 'react-router'

function Navbar() {
  const location = useLocation()
  const path = ()=>{
    let pathname = location.pathname.split('/').join(' / ') //just splited and joined for text style formating propose only
    let shrinkpathname = pathname.split('/')[1]
    let name
    
    if(shrinkpathname == ' '){
      name = 'home'
      pathname = '/ home'
    } 
    else {
      name = shrinkpathname
    }
    return {pathname, name}
  }
  
const[pathData,setPathData] = useState()  
useEffect(()=>{
  const {pathname, name} = path()
  setPathData({pathname, name})
},[location.pathname])

  

  //Scroll event for conditional styling
  const [onScroll, setOnScroll] = useState(false)

  window.addEventListener('scroll', ()=> {
    if(scrollY > 0 ){
      setOnScroll(true)
    } else{
      setOnScroll(false)
    }
  })

  return (
    <nav className={`sticky top-0 z-50 w-full flex items-center justify-between p-4 transition ease-in-out duration-150 ${onScroll ? 'bg-[rgba(255,255,255,0.9)] backdrop-blur shadow-lg' : 'bg-transparent'}`}>
      <div>
        <div className='flex items-center space-x-2'>
          <HiHome className='text-gray-400 text-base' />
          <span className='capitalize font-semibold text-title-color'>{pathData?.pathname}</span>
        </div>
        {/*<h4 className='capitalize font-bold text-lg text-title-color hidden md:block'>{pathData?.name}</h4>*/}
      </div>
      <div className='flex justify-between items-center md:w-[400px]'>
        <form className='relative hidden md:block'>
          <input className='border-2 bg-transparent p-2.5 rounded-sm' type="text" placeholder='Search here'/>
        </form>
        <div className='flex justify-between shrink-0 w-[100px] cursor-pointer'>
            <HiUserCircle className={`${onScroll ? 'text-title-color' : 'text-gray-500'} text-[20px]`} />
            <HiCog6Tooth className={`${onScroll ? 'text-title-color' : 'text-gray-500'} text-[20px]`} />
            <HiPower className={`${onScroll ? 'text-title-color' : 'text-gray-500'} text-[20px]  hover:text-red-600`} title='sign out' width={"20px"}/>
        </div>
      </div>
    </nav>
  )
}

export default Navbar