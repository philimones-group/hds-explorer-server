/* eslint-disable react-hooks/rules-of-hooks */
/* eslint-disable react/prop-types */
import { useState } from 'react' 
import {HiMenuAlt3, HiOutlineChartPie, HiOutlineLogout, HiOutlineLogin} from 'react-icons/hi'
import { useLocation, useNavigate } from 'react-router-dom'
import { AiOutlineClose, AiOutlineUserAdd, AiOutlineUserDelete ,AiOutlineUserSwitch} from 'react-icons/ai'
import {BsHouseAdd, BsHouseCheck, BsPeople} from 'react-icons/bs'
import {MdOutlinePregnantWoman} from 'react-icons/md'
import {BiChild} from 'react-icons/bi'

function Sidebar({i18n, layout}) {
  
  // eslint-disable-next-line no-unused-vars
  const navigate = useNavigate()
  const location = (path) => {
    const currentpath = useLocation()
    return currentpath.pathname.split('/')[1] == path
}

// Collapse Sidebar
const [collapse, setCollapse] = useState(false)
const handleCollapse = ()=>{
  
  setCollapse(!collapse)
  if(collapse){
    layout('sidebar-collapse')
  }
  if(!collapse){
    layout('sidebar-expand')
  }

}

//Menu button
const [showMenu, setShowMenu] = useState(false)

  return (
    <aside className='sticky top-0 z-50 bg-sidebar md:min-h-[100vh] p-4'>
      <div className='flex justify-between'>
        {collapse && <div className='w-full md:h-28 md:text-2xl text-white md:text-center md:p-8 font-bold shadow-sm'>HDS</div>}
        <button className='md:hidden' onClick={()=> setShowMenu(!showMenu)}>
          {!showMenu && <HiMenuAlt3 className='text-white text-2xl'/>}
          {showMenu && <AiOutlineClose className='text-white text-2xl' />}
        </button>
      </div>
      <nav>
        <ul className={`${!showMenu ? 'hidden': 'block h-screen mt-10'} md:block`}>
          <li className={`sidebar-item ${location('')?'bg-sidebar-clicked hover:bg-sidebar-hover shadow-2xl font-semibold':'bg-transparent font-thin'}`} /*onClick={()=>{navigate('/'); setShowMenu(false)}}*/>
            <HiOutlineChartPie className={!collapse? 'sidebar-collapsed-icon' : 'sidebar-icon'}/>
            <span className={!collapse ? 'hidden': ''}>{i18n['dashboard.collectionStatus']}</span>
          </li>
          <li className={`sidebar-item ${location('household-registration')?'bg-sidebar-clicked hover:bg-sidebar-hover shadow-2xl font-semibold':'bg-transparent font-thin'}`} /*onClick={()=>{navigate('/household-registration'); setShowMenu(false)}}*/>
            <BsHouseAdd className={!collapse? 'sidebar-collapsed-icon' : 'sidebar-icon'}/>
            <span className={!collapse ? 'hidden': ''}>{i18n['dashboard.householdRegistration']}</span>
          </li>
          <li className={`sidebar-item ${location('household-visit')?'bg-sidebar-clicked hover:bg-sidebar-hover shadow-2xl font-semibold':'bg-transparent font-thin'}`} /*onClick={()=>{navigate('/household-visit') ; setShowMenu(false)}}*/>
            <BsHouseCheck className={!collapse? 'sidebar-collapsed-icon' : 'sidebar-icon'}/>
            <span className={!collapse ? 'hidden': ''}>{i18n['dashboard.householdVisit']}</span>
          </li>
          <li className={`sidebar-item ${location('member-enumeration')?'bg-sidebar-clicked hover:bg-sidebar-hover shadow-2xl font-semibold':'bg-transparent font-thin'}`} /*onClick={()=>{navigate('/member-enumeration') ; setShowMenu(false)}}*/>
            <AiOutlineUserAdd className={!collapse? 'sidebar-collapsed-icon' : 'sidebar-icon'}/>
            <span className={!collapse ? 'hidden': ''}>{i18n['dashboard.memberEnumeration']}</span>
          </li>
          <li className={`sidebar-item ${location('change-head-household')?'bg-sidebar-clicked hover:bg-sidebar-hover shadow-2xl font-semibold':'bg-transparent font-thin'}`} /*onClick={()=>{navigate('/change-head-household') ; setShowMenu(false)}}*/>
            <AiOutlineUserSwitch className={!collapse? 'sidebar-collapsed-icon' : 'sidebar-icon'}/>
            <span className={!collapse ? 'hidden': ''}>{i18n['dashboard.changeHeadHousehold']}</span>
          </li>
          <li className={`sidebar-item ${location('marital-relationship')?'bg-sidebar-clicked hover:bg-sidebar-hover shadow-2xl font-semibold':'bg-transparent font-thin'}`} /*onClick={()=>{navigate('/marital-relationship') ; setShowMenu(false)}}*/>
            <BsPeople className={!collapse? 'sidebar-collapsed-icon' : 'sidebar-icon'}/>
            <span className={!collapse ? 'hidden': ''}>{i18n['dashboard.maritalRelationship']}</span>
          </li>
          <li className={`sidebar-item ${location('external-in-migration')?'bg-sidebar-clicked hover:bg-sidebar-hover shadow-2xl font-semibold':'bg-transparent font-thin'}`} /*onClick={()=>{navigate('/external-in-migration') ; setShowMenu(false)}}*/>
            <HiOutlineLogout className={!collapse? 'sidebar-collapsed-icon' : 'sidebar-icon'}/>
            <span className={!collapse ? 'hidden': ''}>{i18n['dashboard.externalInMigration']}</span>
          </li>
          <li className={`sidebar-item ${location('internal-in-migration')?'bg-sidebar-clicked hover:bg-sidebar-hover shadow-2xl font-semibold':'bg-transparent font-thin'}`} /*onClick={()=>{navigate('/internal-in-migration') ; setShowMenu(false)}}*/>
            <HiOutlineLogin className={!collapse? 'sidebar-collapsed-icon' : 'sidebar-icon'}/>
            <span className={!collapse ? 'hidden': ''}>{i18n['dashboard.internalInMigration']}</span>
          </li>
          <li className={`sidebar-item ${location('death-registration')?'bg-sidebar-clicked hover:bg-sidebar-hover shadow-2xl font-semibold':'bg-transparent font-thin'}`} /*onClick={()=>{navigate('/death-registration') ; setShowMenu(false)}}*/>
            <AiOutlineUserDelete className={!collapse? 'sidebar-collapsed-icon' : 'sidebar-icon'}/>
            <span className={!collapse ? 'hidden': ''}>{i18n['dashboard.deathRegistration']}</span>
          </li>
          <li className={`sidebar-item ${location('pregnancy-registration')?'bg-sidebar-clicked hover:bg-sidebar-hover shadow-2xl font-semibold':'bg-transparent font-thin'}`} /*onClick={()=>{navigate('/pregnancy-registration') ; setShowMenu(false)}}*/>
            <MdOutlinePregnantWoman className={!collapse? 'sidebar-collapsed-icon' : 'sidebar-icon'}/>
            <span className={!collapse ? 'hidden': ''}>{i18n['dashboard.pregnancyRegistration']}</span>
          </li>
          <li className={`sidebar-item ${location('birth-registration')?'bg-sidebar-clicked hover:bg-sidebar-hover shadow-2xl font-semibold':'bg-transparent font-thin'}`} /*onClick={()=>{navigate('/birth-registration') ; setShowMenu(false)}}*/>
            <BiChild className={!collapse? 'sidebar-collapsed-icon' : 'sidebar-icon'}/>
            <span className={!collapse ? 'hidden': ''}>{i18n['dashboard.birthRegistration']}</span>
          </li>
          <li className={`sidebar-item-last hidden md:block`} onClick={handleCollapse}>{!collapse ? <span className='text-[10px]'>{i18n['dashboard.expand']}</span> : <span>{i18n['dashboard.collapse']}</span>}</li>
        </ul>
      </nav>
    </aside>
  )
}

export default Sidebar