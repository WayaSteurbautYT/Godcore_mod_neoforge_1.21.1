import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';

const Home=(p)=><svg viewBox='0 0 24 24' fill='none' stroke='currentColor' strokeWidth='2' {...p}><path d='M3 11l9-8 9 8'/><path d='M5 10v10h14V10'/></svg>;
const Cpu=(p)=><svg viewBox='0 0 24 24' fill='none' stroke='currentColor' strokeWidth='2' {...p}><rect x='4' y='4' width='16' height='16' rx='2'/><rect x='9' y='9' width='6' height='6'/><path d='M9 1v4'/><path d='M15 1v4'/><path d='M9 19v4'/><path d='M15 19v4'/><path d='M1 9h4'/><path d='M1 15h4'/><path d='M19 9h4'/><path d='M19 15h4'/></svg>;
const BookOpen=(p)=><svg viewBox='0 0 24 24' fill='none' stroke='currentColor' strokeWidth='2' {...p}><path d='M2 5h8a4 4 0 014 4v10H6a4 4 0 00-4 4z'/><path d='M22 5h-8a4 4 0 00-4 4v10h8a4 4 0 014 4z'/></svg>;
const Search=(p)=><svg viewBox='0 0 24 24' fill='none' stroke='currentColor' strokeWidth='2' {...p}><circle cx='11' cy='11' r='7'/><path d='M21 21l-4.3-4.3'/></svg>;
const Star=(p)=><svg viewBox='0 0 24 24' fill='none' stroke='currentColor' strokeWidth='2' {...p}><path d='M12 2l3.1 6.3 6.9 1-5 4.8 1.2 6.9L12 17.8 5.8 21l1.2-6.9-5-4.8 6.9-1z'/></svg>;
const MessageSquare=(p)=><svg viewBox='0 0 24 24' fill='none' stroke='currentColor' strokeWidth='2' {...p}><path d='M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z'/></svg>;

const routes=['home','wiki','downloads','media','docs','commands','personalities'];
const recipes=[{name:'AI Core',grid:['G','G','G','C','B','C',' ',' ',' ']}];
const items = {
  home:[
    {name:'AI Core', rarity:'LEGENDARY', icon:'🧠', lore:'The heart of Godcore AI', stat:'Intelligence +100'},
    {name:'Oracle Staff', rarity:'EPIC', icon:'🔮', lore:'Wise Oracle personality weapon', stat:'Knowledge +50'},
    {name:'Builder Hammer', rarity:'RARE', icon:'🔨', lore:'Friendly Builder personality tool', stat:'Building Speed +30'}
  ],
  commands:[
    {name:'Activate Command', rarity:'COMMON', icon:'⚡', lore:'/god activate - Start AI', stat:'Core System'},
    {name:'Chat Command', rarity:'COMMON', icon:'💬', lore:'/god chat <msg> - Talk to AI', stat:'Communication'},
    {name:'Summon Command', rarity:'UNCOMMON', icon:'👻', lore:'/god summon - Spawn entity', stat:'Entity System'},
    {name:'Config Command', rarity:'RARE', icon:'⚙️', lore:'/god config - Open settings', stat:'GUI System'}
  ],
  personalities:[
    {name:'Wise Oracle', rarity:'LEGENDARY', icon:'🦉', lore:'Ancient wisdom personality', stat:'Knowledge Focused'},
    {name:'Friendly Builder', rarity:'EPIC', icon:'🏗️', lore:'Creative builder personality', stat:'Construction Focused'},
    {name:'Guardian Knight', rarity:'RARE', icon:'🛡️', lore:'Protective guardian personality', stat:'Defense Focused'},
    {name:'Chaotic Trickster', rarity:'EPIC', icon:'🃏', lore:'Unpredictable trickster', stat:'Chaos Focused'},
    {name:'Waya Mode', rarity:'LEGENDARY', icon:'🌟', lore:'Creator personality mode', stat:'All Stats +50'}
  ],
  wiki:[
    {name:'Installation Guide', rarity:'COMMON', icon:'📖', lore:'How to install the mod', stat:'Documentation'},
    {name:'API Configuration', rarity:'RARE', icon:'🔑', lore:'Setup Gemini API key', stat:'Integration'},
    {name:'Permission System', rarity:'UNCOMMON', icon:'🔐', lore:'Player tier permissions', stat:'Security'}
  ]
};

function Slot({item}){
  const [hover,setHover]=useState(false);
  return <div className='relative'>
    <motion.button whileTap={{scale:0.95}} onMouseEnter={()=>setHover(true)} onMouseLeave={()=>setHover(false)} className='w-16 h-16 md:w-20 md:h-20 bg-zinc-600 border-4 border-zinc-800 shadow-[inset_-2px_-2px_0_#a1a1aa,inset_2px_2px_0_#27272a] flex items-center justify-center text-3xl relative overflow-hidden'>
      <span>{item.icon}</span>
      <div className='absolute inset-0 bg-gradient-to-r from-transparent via-white/30 to-transparent animate-pulse -skew-x-12'></div>
    </motion.button>
    {hover && <div className='absolute z-20 left-0 top-20 w-56 bg-black/95 border-2 border-fuchsia-700 p-3 text-left text-xs font-mono'>
      <div className='text-cyan-300'>{item.name}</div>
      <div className='text-green-400'>{item.stat}</div>
      <div className='text-zinc-300'>{item.lore}</div>
      <div className='text-yellow-300 mt-1'>{item.rarity}</div>
    </div>}
  </div>
}

export default function App(){
 if (typeof document !== 'undefined') {
  document.title='Godcore Mod Portal';
  const meta=document.querySelector("meta[name='description']") || (()=>{const m=document.createElement('meta');m.name='description';document.head.appendChild(m);return m;})();
  meta.content='Official portal for Godcore NeoForge 1.21.1 mod - AI companion for Minecraft.';
 }
 const [repo,setRepo]=useState(null);
 const [loading,setLoading]=useState(false);
 async function loadGitHub(){ setLoading(true); try{ const r=await fetch('https://api.github.com/repos/WayaSteurbautYT/Godcore_mod_neoforge_1.21.1'); const data=await r.json(); setRepo(data); const rr=await fetch('https://api.github.com/repos/WayaSteurbautYT/Godcore_mod_neoforge_1.21.1/releases/latest'); const rel=await rr.json(); setRelease(rel); const rc=await fetch('https://api.github.com/repos/WayaSteurbautYT/Godcore_mod_neoforge_1.21.1/commits'); const cc=await rc.json(); setCommits(Array.isArray(cc)?cc.slice(0,5):[]); if(data.default_branch){ try{ const rd=await fetch('https://raw.githubusercontent.com/WayaSteurbautYT/Godcore_mod_neoforge_1.21.1/'+data.default_branch+'/README.md'); const txt=await rd.text(); setDocs(txt.slice(0,2500)); }catch(e){} } if(rel && rel.assets){ setAssets(rel.assets); }}catch(e){} setLoading(false);} 
 const [page,setPage]=useState('home');
 const [recipeOpen,setRecipeOpen]=useState(false);
 const [theme,setTheme]=useState('dark');
 const [query,setQuery]=useState('');
 const [release,setRelease]=useState(null);
 const [commits,setCommits]=useState([]);
 const [docs,setDocs]=useState('# Godcore Mod\nInstallation guide loading...');
 const [assets,setAssets]=useState([]);
 const [media]=useState(['screenshot1.png','screenshot2.png','trailer-thumb.png']);
 const [searchAll,setSearchAll]=useState('');
 const [ready,setReady]=useState(false);
 React.useEffect(()=>{loadGitHub()},[]);
 useEffect(()=>{ if(typeof window!=='undefined'){ window.location.hash=page; localStorage.setItem('godcore_theme',theme);} },[page,theme]);
 useEffect(()=>{ if(typeof window!=='undefined'){ const t=localStorage.getItem('godcore_theme'); if(t) setTheme(t); setReady(true);} },[]);
 const filtered=(items[page]||items.home).filter(i=>i.name.toLowerCase().includes(query.toLowerCase()) || i.lore.toLowerCase().includes(query.toLowerCase()));
 const themeClass = theme==='dark' ? 'bg-gradient-to-b from-zinc-950 via-zinc-900 to-black' : 'bg-gradient-to-b from-slate-200 via-white to-slate-100 text-black';
 return (ready && 
 <div className={'min-h-screen text-white p-4 font-mono transition-all duration-300 ' + themeClass}>
   <div className='max-w-6xl mx-auto'>
     <div className='mb-4 grid md:grid-cols-4 gap-3'>
       <div className='rounded-2xl bg-zinc-900/80 p-4 border border-zinc-700'><div className='text-sm text-zinc-400'>Portal Status</div><div className='text-2xl font-bold text-cyan-300'>ONLINE</div></div>
       <div className='rounded-2xl bg-zinc-900/80 p-4 border border-zinc-700'><div className='text-sm text-zinc-400'>Game Version</div><div className='text-2xl font-bold text-yellow-300'>1.21.1</div></div>
       <div className='rounded-2xl bg-zinc-900/80 p-4 border border-zinc-700'><div className='text-sm text-zinc-400'>Platform</div><div className='text-2xl font-bold text-fuchsia-300'>NEOFORGE</div></div>
       <div className='rounded-2xl bg-zinc-900/80 p-4 border border-zinc-700'><div className='text-sm text-zinc-400'>Build</div><div className='text-2xl font-bold text-emerald-300'>LIVE</div></div>
     </div>
     <div className='rounded-2xl border border-zinc-700 bg-zinc-900/80 backdrop-blur p-4 shadow-2xl'>
       <div className='grid md:grid-cols-5 gap-2 mb-4 text-sm'>
         <a href='https://github.com/WayaSteurbautYT/Godcore_mod_neoforge_1.21.1' target='_blank' className='rounded-xl bg-emerald-700 hover:bg-emerald-600 p-2 text-center'>GitHub</a>
         <button className='rounded-xl bg-blue-700 hover:bg-blue-600 p-2'>Downloads</button>
         <button className='rounded-xl bg-zinc-800 hover:bg-zinc-700 p-2'>Docs</button>
         <button className='rounded-xl bg-zinc-800 hover:bg-zinc-700 p-2'>Media</button>
         <button className='rounded-xl bg-zinc-800 hover:bg-zinc-700 p-2'>Community</button>
         <button onClick={()=>setTheme(theme==='dark'?'light':'dark')} className='rounded-xl bg-amber-600 hover:bg-amber-500 p-2'>Theme</button>
       </div>
       <div className='flex flex-col md:flex-row md:items-center gap-3 justify-between'>
         <div>
           <h1 className='text-3xl font-bold tracking-wide'>GODCORE MOD PORTAL</h1>
           <p className='text-zinc-400 text-sm'>AI companion mod for Minecraft NeoForge 1.21.1 - Chat, build, and explore with an intelligent AI</p>
         </div>
         <div className='flex items-center gap-2 bg-zinc-800 rounded-xl px-3 py-2'>
           <Search size={16}/><input value={query} onChange={e=>{setQuery(e.target.value);setSearchAll(e.target.value);}} placeholder='Search portal...' className='bg-transparent outline-none text-sm'/>
         </div>
       </div>
       <div className='grid grid-cols-2 md:grid-cols-4 gap-2 mt-4'>
         {routes.map(r=><button key={r} onClick={()=>setPage(r)} className='p-2 rounded-xl bg-zinc-800 hover:bg-zinc-700 capitalize'>{r}</button>)}
       </div>
       <div className='md:col-span-4 grid grid-cols-4 gap-2 mt-2'>
         <button onClick={()=>setPage('home')} className='p-2 rounded-xl bg-zinc-800 hover:bg-zinc-700 flex items-center justify-center gap-2'><Home size={16}/>Menu</button>
         <button onClick={()=>setPage('commands')} className='p-2 rounded-xl bg-zinc-800 hover:bg-zinc-700 flex items-center justify-center gap-2'><MessageSquare size={16}/>Commands</button>
         <button onClick={()=>setPage('personalities')} className='p-2 rounded-xl bg-zinc-800 hover:bg-zinc-700 flex items-center justify-center gap-2'><Cpu size={16}/>Personalities</button>
         <button onClick={()=>setPage('wiki')} className='p-2 rounded-xl bg-zinc-800 hover:bg-zinc-700 flex items-center justify-center gap-2'><BookOpen size={16}/>Wiki</button>
       </div>
       <div className='mt-5 grid grid-cols-3 sm:grid-cols-4 md:grid-cols-6 lg:grid-cols-8 gap-3'>
         {page==='downloads' && <div className='col-span-full rounded-2xl bg-zinc-900 p-4'>Download center: use buttons above and GitHub releases. Ready for asset autodetect.</div>}
         {page==='media' && <div className='col-span-full rounded-2xl bg-zinc-900 p-4'><div className='font-semibold mb-3'>Media Gallery</div><div className='grid md:grid-cols-3 gap-3'>{media.map((m,i)=><div key={i} className='rounded-xl bg-zinc-800 p-2 text-sm text-center'><img src={'/media/'+m} onError={(e)=>e.currentTarget.style.display='none'} className='rounded-lg mb-2 w-full h-40 object-cover'/>{m}<div className='text-zinc-500 text-xs mt-2'>/public/media/{m}</div></div>)}</div></div>}
         {page==='wiki' && <div className='col-span-full rounded-2xl bg-zinc-900 p-4'><button onClick={()=>setRecipeOpen(true)} className='px-3 py-2 rounded bg-emerald-700'>Open Crafting Recipe</button></div>}
         {page==='docs' && <div className='col-span-full rounded-2xl bg-zinc-900 p-4'><div className='font-semibold mb-2'>Documentation</div><div className='text-sm text-zinc-400'><div className='whitespace-pre-wrap text-xs text-zinc-300 max-h-80 overflow-auto leading-6'>{docs}</div><div className='mt-3 text-sm text-zinc-400'>README live import enabled.</div>{assets.length>0 && <div className='mt-3 space-y-2'>{assets.map((a,i)=><a key={i} href={a.browser_download_url} target='_blank' className='block rounded-xl bg-zinc-800 p-2'>{a.name}</a>)}</div>}</div></div>}
         {filtered.map((item,idx)=><Slot key={idx} item={item}/>)}
       </div>
       {recipeOpen && <div className='fixed inset-0 bg-black/70 flex items-center justify-center p-4 z-50'><div className='bg-zinc-900 rounded-2xl p-6 max-w-md w-full'><div className='font-bold mb-3'>Crafting Recipe</div><div className='grid grid-cols-3 gap-2'>{recipes[0].grid.map((c,i)=><div key={i} className='h-14 rounded bg-zinc-800 flex items-center justify-center'>{c}</div>)}</div><button onClick={()=>setRecipeOpen(false)} className='mt-4 px-3 py-2 rounded bg-rose-700'>Close</button></div></div>}
       <div className='mt-6 grid md:grid-cols-3 gap-4'>
         <div className='rounded-2xl bg-zinc-800 p-4 md:col-span-3'>
           <div className='flex items-center justify-between'>
             <div className='font-semibold text-yellow-300'>GitHub Dashboard</div>
             <button onClick={loadGitHub} className='px-3 py-1 rounded-xl bg-zinc-700 hover:bg-zinc-600 text-sm'>{loading?'Loading...':'Refresh'}</button>
           </div>
           {repo && <div className='grid md:grid-cols-4 gap-3 mt-3 text-sm'>
             <div className='rounded-xl bg-zinc-900 p-3'>⭐ Stars: {repo.stargazers_count||0}</div>
             <div className='rounded-xl bg-zinc-900 p-3'>🍴 Forks: {repo.forks_count||0}</div>
             <div className='rounded-xl bg-zinc-900 p-3'>🐞 Issues: {repo.open_issues_count||0}</div>
             <div className='rounded-xl bg-zinc-900 p-3 text-fuchsia-300'>📦 Version: {(release&& (release.tag_name||release.name)) || 'None'}</div>
           </div>}
           {repo && <p className='text-zinc-400 text-sm mt-3'>{repo.description}</p>}
           {release && <div className='mt-3 rounded-xl bg-zinc-900 p-3 text-sm'>
             <div className='text-cyan-300'>Latest Release Notes</div>
             <div className='text-zinc-400 mt-1'>{(release.body||'No notes').slice(0,220)}</div>
           </div>}
           {commits.length>0 && <div className='mt-3 rounded-xl bg-zinc-900 p-3 text-sm'>
             <div className='text-green-300 mb-2'>Recent Commits</div>
             <div className='space-y-2'>{commits.map((c,i)=><div key={i} className='border-b border-zinc-800 pb-2'><div className='text-zinc-300'>{c.commit.message}</div><div className='text-zinc-500 text-xs'>{c.commit.author.name}</div></div>)}</div>
           </div>}
           <div className='mt-3 grid md:grid-cols-3 gap-3 text-sm'>
             <a href='https://github.com/WayaSteurbautYT/Godcore_mod_neoforge_1.21.1' target='_blank' className='rounded-xl bg-emerald-700 hover:bg-emerald-600 p-3 text-center'>Open Repo</a>
             <a href='https://github.com/WayaSteurbautYT/Godcore_mod_neoforge_1.21.1/releases' target='_blank' className='rounded-xl bg-blue-700 hover:bg-blue-600 p-3 text-center'>Downloads</a>
             <a href='https://github.com/WayaSteurbautYT/Godcore_mod_neoforge_1.21.1/issues' target='_blank' className='rounded-xl bg-rose-700 hover:bg-rose-600 p-3 text-center'>Issues</a>
           </div>
         </div>
         <div className='rounded-2xl bg-zinc-800 p-4'><div className='flex gap-2 items-center'><Star size={16}/>Features</div><p className='text-sm text-zinc-400 mt-2'>AI chat system, multiple personalities, world actions, approval system, undo/rollback, permission tiers, and more.</p></div>
         <div className='rounded-2xl bg-zinc-800 p-4'><div className='font-semibold'>README Embed</div><p className='text-sm text-zinc-400 mt-2'>Use screenshots or deploy with GitHub Pages for live widgets.</p></div>
         <div className='rounded-2xl bg-zinc-800 p-4'><div className='font-semibold'>Tech Stack</div><p className='text-sm text-zinc-400 mt-2'>React + Tailwind + Framer Motion + GitHub API + NeoForge 1.21.1 + Java 21</p></div>
       </div>
     </div>
   </div>
 </div>
 )
}
