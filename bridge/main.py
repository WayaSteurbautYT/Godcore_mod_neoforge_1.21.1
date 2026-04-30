"""
Godcore Bridge App
Handles Gemini API calls, memory, and tool execution for the Godcore Minecraft mod.
Communicates with the mod via WebSocket on port 8080.
"""

import asyncio
import websockets
import json
import google.generativeai as genai
from typing import Dict, Any
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class GodcoreBridge:
    def __init__(self):
        self.api_keys: Dict[str, str] = {}
        self.system_prompts: Dict[str, str] = {}
        self.memory: Dict[str, Any] = {}
        self.model_name = "gemini-1.5-flash"  # Free model
        
    async def handle_message(self, websocket, message: str):
        """Handle incoming messages from the Minecraft mod"""
        try:
            data = json.loads(message)
            message_type = data.get("type")
            
            logger.info(f"Received {message_type} request")
            
            if message_type == "chat":
                response = await self.handle_chat(data)
                await websocket.send(json.dumps({"type": "chat_response", "data": response}))
                
            elif message_type == "code":
                response = await self.handle_code(data)
                await websocket.send(json.dumps({"type": "code_response", "data": response}))
                
            elif message_type == "gui":
                response = await self.handle_gui(data)
                await websocket.send(json.dumps({"type": "gui_response", "data": response}))
                
            elif message_type == "action":
                response = await self.handle_action(data)
                await websocket.send(json.dumps({"type": "action_response", "data": response}))
                
            elif message_type == "test":
                response = await self.handle_test(data)
                await websocket.send(json.dumps({"type": "test_response", "data": response}))
                
            else:
                logger.warning(f"Unknown message type: {message_type}")
                
        except Exception as e:
            logger.error(f"Error handling message: {e}")
            await websocket.send(json.dumps({"type": "error", "data": str(e)}))
    
    async def handle_chat(self, data: Dict[str, Any]) -> str:
        """Handle chat requests"""
        api_key = data.get("apiKey")
        model = data.get("model", self.model_name)
        system_prompt = data.get("systemPrompt", "")
        user_message = data.get("userMessage", "")
        context = data.get("context", "")
        
        try:
            genai.configure(api_key=api_key)
            model_client = genai.GenerativeModel(model)
            
            full_prompt = f"{system_prompt}\n\nContext: {context}\n\nUser: {userMessage}"
            
            response = model_client.generate_content(full_prompt)
            return response.text
            
        except Exception as e:
            logger.error(f"Chat error: {e}")
            return f"Error: {str(e)}"
    
    async def handle_code(self, data: Dict[str, Any]) -> str:
        """Handle code generation requests"""
        api_key = data.get("apiKey")
        model = data.get("model", self.model_name)
        system_prompt = data.get("systemPrompt", "")
        description = data.get("description", "")
        
        try:
            genai.configure(api_key=api_key)
            model_client = genai.GenerativeModel(model)
            
            prompt = f"{system_prompt}\n\nGenerate Java code for: {description}\n\nReturn only the code, no explanations."
            
            response = model_client.generate_content(prompt)
            return response.text
            
        except Exception as e:
            logger.error(f"Code generation error: {e}")
            return f"// Error: {str(e)}"
    
    async def handle_gui(self, data: Dict[str, Any]) -> str:
        """Handle GUI schema generation requests"""
        api_key = data.get("apiKey")
        model = data.get("model", self.model_name)
        system_prompt = data.get("systemPrompt", "")
        description = data.get("description", "")
        
        try:
            genai.configure(api_key=api_key)
            model_client = genai.GenerativeModel(model)
            
            prompt = f"{system_prompt}\n\nGenerate a JSON GUI schema for: {description}\n\nReturn only valid JSON."
            
            response = model_client.generate_content(prompt)
            return response.text
            
        except Exception as e:
            logger.error(f"GUI generation error: {e}")
            return "{}"
    
    async def handle_action(self, data: Dict[str, Any]) -> str:
        """Handle action decision requests"""
        api_key = data.get("apiKey")
        model = data.get("model", self.model_name)
        system_prompt = data.get("systemPrompt", "")
        situation = data.get("situation", "")
        
        try:
            genai.configure(api_key=api_key)
            model_client = genai.GenerativeModel(model)
            
            prompt = f"{system_prompt}\n\nDecide the best action for this situation: {situation}\n\nRespond with one word: wait, build, fight, mine, patrol, or follow."
            
            response = model_client.generate_content(prompt)
            return response.text.strip().lower()
            
        except Exception as e:
            logger.error(f"Action decision error: {e}")
            return "wait"
    
    async def handle_test(self, data: Dict[str, Any]) -> Dict[str, Any]:
        """Handle connection test requests"""
        api_key = data.get("apiKey", "")
        
        try:
            genai.configure(api_key=api_key)
            model_client = genai.GenerativeModel(self.model_name)
            response = model_client.generate_content("Respond with 'OK'")
            
            success = "OK" in response.text
            return {"success": success, "message": "Connection successful" if success else "Connection failed"}
            
        except Exception as e:
            logger.error(f"Test error: {e}")
            return {"success": False, "message": str(e)}

async def main():
    """Main WebSocket server"""
    bridge = GodcoreBridge()
    
    async with websockets.serve(bridge.handle_message, "localhost", 8080):
        logger.info("Godcore Bridge server started on ws://localhost:8080")
        logger.info("Waiting for connections from Minecraft mod...")
        await asyncio.Future()  # Run forever

if __name__ == "__main__":
    asyncio.run(main())
